/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Craftory.customBlockManager;
import static tech.brettsaunders.craftory.Utilities.getChunkID;
import static tech.brettsaunders.craftory.Utilities.getChunkWorldID;
import static tech.brettsaunders.craftory.Utilities.getLocationID;
import static tech.brettsaunders.craftory.Utilities.getRegionID;
import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.tools.ToolLevel;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Log;

public class CustomBlockManager {

  public static final String DATA_FOLDER =
      Craftory.plugin.getDataFolder() + File.separator + "data";
  private final HashMap<Location, CustomBlock> currentCustomBlocks;
  @Getter
  private final ConcurrentHashMap<String, HashSet<CustomBlock>> activeChunks;
  @Getter
  private final ConcurrentHashMap<String, HashSet<CustomBlock>> inactiveChunks;
  @Getter
  private final HashMap<String, CustomBlockData> customBlockDataHashMap;

  private final PersistenceStorage persistenceStorage;

  public CustomBlockManager() {
    persistenceStorage = new PersistenceStorage();
    currentCustomBlocks = new HashMap<>();
    customBlockDataHashMap = new HashMap<>();
    activeChunks = new ConcurrentHashMap<>();
    inactiveChunks = new ConcurrentHashMap<>();
    loadCustomBlockData();
    new CustomBlockManagerEvents(persistenceStorage,
        currentCustomBlocks,
        activeChunks,
        inactiveChunks,
        customBlockDataHashMap);
  }

  public CustomBlock getCustomBlock(Location location) {
    return currentCustomBlocks.get(location);
  }

  public CustomBlock placeCustomBlock(String customBlockItemName, Block block,
      BlockFace direction, Player player) {
    CustomBlock customBlock = Craftory.customBlockFactory
        .create(customBlockItemName, block.getLocation(), direction, player);
    if (customBlock.getDirection() != BlockFace.NORTH) {
      customBlockItemName = customBlockItemName + "_" + customBlock.getDirection().name();
    }
    generateCustomBlock(customBlockItemName, block);
    putActiveCustomBlock(customBlock);
    Craftory.tickManager.addTickingBlock(customBlock);
    return customBlock;
  }

  public void placeBasicCustomBlock(String customBlockItemName, Block block) {
    generateCustomBlock(customBlockItemName, block);
  }

  private void generateCustomBlock(String customBlockItemName, Block block) {
    CustomBlockData data = customBlockDataHashMap.get(customBlockItemName);
    block.setType(Material.MUSHROOM_STEM, false);

    BlockData blockData = block.getBlockData();
    MultipleFacing multipleFacing = (MultipleFacing) blockData;

    multipleFacing.setFace(
        BlockFace.UP, data.up);
    multipleFacing.setFace(BlockFace.DOWN, data.down);
    multipleFacing.setFace(BlockFace.NORTH, data.north);
    multipleFacing.setFace(BlockFace.EAST, data.east);
    multipleFacing.setFace(BlockFace.SOUTH, data.south);
    multipleFacing.setFace(BlockFace.WEST, data.west);
    block.setBlockData(multipleFacing, false);
  }

  public void putActiveCustomBlock(CustomBlock customBlock) {
    if (customBlock == null) {
      Log.warn("Custom Block is null");
      return;
    }
    String chunkID = getChunkWorldID(customBlock.location.getChunk());
    HashSet<CustomBlock> chunkData;
    if (activeChunks.containsKey(chunkID)) {
      chunkData = activeChunks.get(chunkID);
    } else {
      chunkData = new HashSet<>();
      addActiveChunk(customBlock);
    }
    chunkData.add(customBlock);
    activeChunks.put(chunkID, chunkData);
    currentCustomBlocks.put(customBlock.location, customBlock);
  }

  public void onDisable() {
    CustomBlockStorage
        .saveAllCustomChunks(DATA_FOLDER, persistenceStorage, activeChunks, inactiveChunks, false);
  }

  public void autoSave() {
    CustomBlockStorage
        .saveAllCustomChunks(DATA_FOLDER, persistenceStorage, activeChunks, inactiveChunks, true);
  }
  /* API */
  //Only works in active chunk
  public boolean isCustomBlock(Location location) {
    return currentCustomBlocks.containsKey(location);
  }

  public String getCustomBlockName(Location location) {
    if (isCustomBlock(location)) {
      return currentCustomBlocks.get(location).blockName;
    }
    return "UNKNOWN";
  }

  public boolean isCustomBlockOfType(Location location, String typeName) {
    if (isCustomBlock(location)) {
      return getCustomBlockName(location).equals(typeName);
    }
    return false;
  }

  /* Internal Methods */

  public Optional<ItemStack> breakCustomBlock(Location location) {
    if (currentCustomBlocks.containsKey(location)) {
      CustomBlock customBlock = currentCustomBlocks.get(location);
      customBlock.blockBreak();
      //Return custom item
      final String blockName = currentCustomBlocks.get(
          location).blockName;
      CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(
          location, blockName, customBlock);
      customBlockManager.removeCustomBlock(customBlock);
      Craftory.tickManager.removeTickingBlock(customBlock);
      Bukkit.getPluginManager().callEvent(customBlockBreakEvent);
      location.getBlock().setType(Material.AIR);
      return Optional.of(CustomItemManager.getCustomItem(blockName));
      //If Basic Block
    } else if (location.getBlock().getType() == Material.MUSHROOM_STEM) {
      BlockData blockData = location.getBlock().getBlockData();
      MultipleFacing multipleFacing = (MultipleFacing) blockData;
      for (Entry<String, BasicBlocks> entry : Utilities.getBasicBlockRegistry().entrySet()) {
        String name = entry.getKey();
        BasicBlocks placement = entry.getValue();
        Set<BlockFace> blockFaces = multipleFacing.getFaces();
        HashSet<BlockFace> compareFaces = placement.getAllowedFaces();
        if (blockFaces.containsAll(compareFaces) && compareFaces.containsAll(blockFaces)) {
          location.getBlock().setType(Material.AIR);
          return Optional.of(CustomItemManager.getCustomItem(name));
        }
      }
    }
    return Optional.empty();
  }

  public void removeCustomBlock(CustomBlock block) {
    removeIfLastActiveChunk(block, false);
    currentCustomBlocks.remove(block.location);
    try {
      NBTFile nbtFile = new NBTFile(
          new File(DATA_FOLDER + File.separator + block.location.getWorld().getName(),
              getRegionID(block.location.getChunk())));
      NBTCompound chunk = nbtFile.getCompound(getChunkID(block.location.getChunk()));
      if (chunk != null) {
        String locationName = getLocationID(block.location);
        if (Boolean.TRUE.equals(chunk.hasKey(locationName))) {
          chunk.removeKey(locationName);
        }
      }
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
      sentryLog(e);
    }
  }

  private void addActiveChunk(CustomBlock customBlock) {
    String chunkID = getChunkWorldID(customBlock.location.getChunk());
    HashSet<CustomBlock> customBlocks = activeChunks
        .getOrDefault(chunkID, new HashSet<>());
    customBlocks.add(customBlock);
    activeChunks.put(chunkID, customBlocks);
  }

  private void removeIfLastActiveChunk(CustomBlock customBlock, boolean addToActive) {
    final String chunkID = getChunkWorldID(customBlock.location.getChunk());
    if (activeChunks.containsKey(chunkID)) {
      HashSet<CustomBlock> customBlocks = activeChunks.get(chunkID);
      if (customBlocks.contains(customBlock)) {
        if (customBlocks.size() == 1) {
          activeChunks.remove(chunkID);
        } else {
          customBlocks.remove(customBlock);
          if (addToActive) {
            activeChunks.put(chunkID, customBlocks);
          }
        }
      }
    }
  }

  private void loadCustomBlockData() {
    ConfigurationSection blocks = Craftory.customBlocksConfig.getConfigurationSection("blocks");
    if (blocks == null) {
      return;
    }
    for (String key : blocks.getKeys(false)) {
      ConfigurationSection block = Craftory.customBlocksConfig
          .getConfigurationSection("blocks." + key);
      CustomBlockData data = new CustomBlockData(block.getBoolean("UP"), block.getBoolean("DOWN"),
          block.getBoolean("NORTH"), block.getBoolean("EAST"), block.getBoolean("SOUTH"),
          block.getBoolean("WEST"));
      if (block.contains("BreakLevel")) {
        ToolLevel toolLevel;
        try {
          toolLevel = ToolLevel.valueOf(block.getString("BreakLevel"));
        } catch (IllegalArgumentException | NullPointerException e) {
          toolLevel = ToolLevel.HAND;
        }
        data.breakLevel = toolLevel;
      }
      customBlockDataHashMap.put(key, data);
    }
  }
}