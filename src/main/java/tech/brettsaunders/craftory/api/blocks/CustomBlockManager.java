package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Utilities.getChunkID;
import static tech.brettsaunders.craftory.Utilities.getLocationID;
import static tech.brettsaunders.craftory.Utilities.getRegionID;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class CustomBlockManager implements Listener {

  private final static String DATA_FOLDER =
      Craftory.plugin.getDataFolder() + File.separator + "data";

  private PersistenceStorage persistenceStorage;

  private final HashMap<Location, CustomBlock> currentCustomBlocks;
  private final HashMap<String, HashSet<CustomBlock>> activeChunks;
  private final HashMap<String, HashSet<CustomBlock>> inactiveChunks;

  private final HashMap<String, CustomBlockData> customBlockDataHashMap;

  public CustomBlockManager() {
    persistenceStorage = new PersistenceStorage();
    currentCustomBlocks = new HashMap<>();
    customBlockDataHashMap = new HashMap<>();
    activeChunks = new HashMap<>();
    inactiveChunks = new HashMap<>();
    loadCustomBlockData();
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    if (!CustomItemManager.isCustomBlockItem(e.getItemInHand())) {
      return;
    }
    final String customBlockItemName = CustomItemManager.getCustomItemName(e.getItemInHand());
    if (!customBlockDataHashMap.containsKey(customBlockItemName)) {
      return;
    }
    CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
        e.getBlockPlaced().getLocation(), customBlockItemName, e.getBlockPlaced());
    if (e.isCancelled()) {
      customBlockPlaceEvent.setCancelled(true);
    } else {

      Block block = e.getBlockPlaced();
      getCustomBlock(customBlockItemName, block);

    }
    Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);
  }

  public Block getCustomBlock(String customBlockItemName, Block block) {
    CustomBlockData data = customBlockDataHashMap.get(customBlockItemName);
    block.setType(Material.BROWN_MUSHROOM_BLOCK);

    BlockData blockData = block.getBlockData();
    MultipleFacing multipleFacing = (MultipleFacing) blockData;

    multipleFacing.setFace(
        BlockFace.UP, data.UP);
    multipleFacing.setFace(BlockFace.DOWN, data.DOWN);
    multipleFacing.setFace(BlockFace.NORTH, data.NORTH);
    multipleFacing.setFace(BlockFace.EAST, data.EAST);
    multipleFacing.setFace(BlockFace.SOUTH, data.SOUTH);
    multipleFacing.setFace(BlockFace.WEST, data.WEST);
    block.setBlockData(multipleFacing);

    putActiveCustomBlock(new CustomBlock(block.getLocation(), customBlockItemName));
    return block;
  }

  public void putActiveCustomBlock(CustomBlock customBlock) {
    String chunkID = getChunkID(customBlock.location.getChunk());
    HashSet<CustomBlock> chunkData;
    if (activeChunks.containsKey(chunkID)) {
      chunkData = activeChunks.get(chunkID);
    } else {
      chunkData = new HashSet<>();
      addActiveChunk(customBlock);
    }
    chunkData.add(customBlock);
    activeChunks.put(chunkID,chunkData);
    currentCustomBlocks.put(customBlock.location, customBlock);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onMushroomPhysics(BlockPhysicsEvent event) {
    if ((event.getChangedType() == Material.BROWN_MUSHROOM_BLOCK)) {
      event.setCancelled(true);
      event.getBlock().getState().update(true, false);
    }
  }


  public void onEnable() {
    CustomBlockStorage.loadAllSavedRegions(DATA_FOLDER, this, persistenceStorage);
  }

  public void onDisable() {
    CustomBlockStorage.saveAllCustomChunks(DATA_FOLDER, persistenceStorage, activeChunks, inactiveChunks);
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent e) {
    CustomBlockStorage.saveAllCustomChunks(DATA_FOLDER, persistenceStorage, activeChunks, inactiveChunks);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    final Location location = e.getBlock().getLocation();
    if (currentCustomBlocks.containsKey(location)) {
      //Return custom item
      final String blockName = currentCustomBlocks.get(
          location).blockName;
      CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(
          location, blockName);
      if (e.isCancelled()) {
        customBlockBreakEvent.setCancelled(true);
      } else {
        removeCustomBlock(currentCustomBlocks.get(location));
//        removeIfLastActiveChunk(location);
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
          location.getWorld()
              .dropItemNaturally(location, CustomItemManager.getCustomItem(blockName));
        }
//        activeCustomBlocks.remove(location);
      }
      Bukkit.getPluginManager().callEvent(customBlockBreakEvent);
      e.getBlock().setType(Material.AIR);
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent e) {
    if (inactiveChunks.containsKey(getChunkID(e.getChunk()))) {
      HashSet<CustomBlock> customBlocks = inactiveChunks.get(getChunkID(e.getChunk()));
      customBlocks.forEach(block -> {
        currentCustomBlocks.put(block.location, block);
        if (Craftory.getBlockPoweredManager().isPoweredBlock(block.location)) {
          Craftory.tickManager
              .addTickingBlock(Craftory.getBlockPoweredManager().getPoweredBlock(block.location));
        }
      });
      inactiveChunks.remove(getChunkID(e.getChunk()));
    }
  }

  @EventHandler
  public void onChunkUnLoad(ChunkUnloadEvent e) {
    final String chunkID = getChunkID(e.getChunk());
    if (activeChunks.containsKey(chunkID)) {
      HashSet<CustomBlock> customBlocks = activeChunks.get(chunkID);
      for (CustomBlock customBlock : customBlocks) {
        if (currentCustomBlocks.containsKey(customBlock.location)) {
          currentCustomBlocks.remove(customBlock.location);
          if (Craftory.getBlockPoweredManager().isPoweredBlock(customBlock.location)) {
            Craftory.tickManager
                .removeTickingBlock(Craftory.getBlockPoweredManager().getPoweredBlock(customBlock.location));
          }
        }
      }
      inactiveChunks.put(chunkID, customBlocks);
      activeChunks.remove(chunkID);
    }
  }

  @EventHandler
  public void onPistonExtend(BlockPistonExtendEvent e) {
    e.getBlocks().forEach((block -> {
      if (currentCustomBlocks.containsKey(block.getLocation())) {
        e.setCancelled(true);
        return;
      }
    }));
  }

  @EventHandler
  public void onPistonRetract(BlockPistonRetractEvent e) {
    e.getBlocks().forEach(block -> {
      if (currentCustomBlocks.containsKey(block.getLocation())) {
        e.setCancelled(true);
        return;
      }
    });
  }

  @EventHandler
  public void onCustomBlockInteract(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
      if (currentCustomBlocks.containsKey(e.getClickedBlock().getLocation())) {
        CustomBlockInteractEvent customBlockInteractEvent = new CustomBlockInteractEvent(
            e.getAction(), e.getClickedBlock(), e.getBlockFace(), e.getItem(), e.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(customBlockInteractEvent);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
          e.setCancelled(true); //eeeeee
        }
      }
    }
    return;
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

  private void removeCustomBlock(CustomBlock block) {
    removeIfLastActiveChunk(block, false);
    currentCustomBlocks.remove(block.location);
    try {
      NBTFile nbtFile = new NBTFile(
          new File(DATA_FOLDER + File.separator + block.location.getWorld().getName(),
              getRegionID(block.location.getChunk())));
      NBTCompound chunk = nbtFile.getCompound(getChunkID(block.location.getChunk()));
      if (chunk != null) {
        String locationName = getLocationID(block.location);
        if (chunk.hasKey(locationName)) {
          chunk.removeKey(locationName);
        }
      }
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addActiveChunk(CustomBlock customBlock) {
    HashSet<CustomBlock> customBlocks = activeChunks
        .getOrDefault(getChunkID(customBlock.location.getChunk()), new HashSet<>());
    customBlocks.add(customBlock);
    activeChunks.put(getChunkID(customBlock.location.getChunk()), customBlocks);
  }

  private void removeIfLastActiveChunk(CustomBlock customBlock, Boolean addToActive) {
    final String chunkID = getChunkID(customBlock.location.getChunk());
    if (activeChunks.containsKey(chunkID)) {
      HashSet<CustomBlock> customBlocks = activeChunks.get(chunkID);
      if (customBlocks.contains(customBlock)) {
        if (customBlocks.size() == 1) {
          activeChunks.remove(getChunkID(customBlock.location.getChunk()));
        } else {
          customBlocks.remove(customBlock);
          if (addToActive) {
            activeChunks.put(getChunkID(customBlock.location.getChunk()), customBlocks);
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
      customBlockDataHashMap.put(key, data);
    }
  }
}