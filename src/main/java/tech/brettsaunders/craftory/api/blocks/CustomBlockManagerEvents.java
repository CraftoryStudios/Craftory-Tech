package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Craftory.customBlockManager;
import static tech.brettsaunders.craftory.Utilities.getChunkID;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class CustomBlockManagerEvents implements Listener {

  private PersistenceStorage persistenceStorage;
  private HashMap<Location, CustomBlock> currentCustomBlocks;
  private HashMap<String, HashSet<CustomBlock>> activeChunks;
  private HashMap<String, HashSet<CustomBlock>> inactiveChunks;
  private HashMap<String, CustomBlockData> customBlockDataHashMap;
  private String dataFolder;

  public CustomBlockManagerEvents(PersistenceStorage persistenceStorage,
      HashMap<Location, CustomBlock> currentCustomBlocks,
      HashMap<String, HashSet<CustomBlock>> activeChunks,
      HashMap<String, HashSet<CustomBlock>> inactiveChunks,
      HashMap<String, CustomBlockData> customBlockDataHashMap,
      String dataFolder, CustomBlockManager customBlockManager) {
    this.persistenceStorage = persistenceStorage;
    this.currentCustomBlocks = currentCustomBlocks;
    this.activeChunks = activeChunks;
    this.inactiveChunks = inactiveChunks;
    this.customBlockDataHashMap = customBlockDataHashMap;
    this.dataFolder = dataFolder;
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
    if (!e.isCancelled()) {
      CustomBlock customBlock = customBlockManager.getCustomBlockOfItem(customBlockItemName, e.getBlockPlaced());
      CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
          e.getBlockPlaced().getLocation(), customBlockItemName, e.getBlockPlaced(), customBlock);
      Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);
      Craftory.tickManager.addTickingBlock(customBlock);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onMushroomPhysics(BlockPhysicsEvent event) {
    if ((event.getChangedType() == Material.BROWN_MUSHROOM_BLOCK)) {
      event.setCancelled(true);
      event.getBlock().getState().update(true, false);
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent e) {
    CustomBlockStorage.saveAllCustomChunks(dataFolder, persistenceStorage, activeChunks, inactiveChunks);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    final Location location = e.getBlock().getLocation();
    if (currentCustomBlocks.containsKey(location)) {
      CustomBlock customBlock = currentCustomBlocks.get(location);
      //Return custom item
      final String blockName = currentCustomBlocks.get(
          location).blockName;
      CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(
          location, blockName,customBlock );
      if (e.isCancelled()) {
        customBlockBreakEvent.setCancelled(true);
      } else {
        customBlockManager.removeCustomBlock(customBlock);
        Craftory.tickManager.removeTickingBlock(customBlock);
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
          location.getWorld()
              .dropItemNaturally(location, CustomItemManager.getCustomItem(blockName));
        }
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
        Craftory.tickManager.addTickingBlock(block);
      });
      inactiveChunks.remove(getChunkID(e.getChunk()));
    }
  }

  @EventHandler
  public void onChunkUnLoad(ChunkUnloadEvent e) {
    final String chunkID = getChunkID(e.getChunk());
    if (activeChunks.containsKey(chunkID)) {
      HashSet<CustomBlock> customBlocks = activeChunks.get(chunkID);
      customBlocks.forEach(customBlock -> {
        if (currentCustomBlocks.containsKey(customBlock.location)) {
          currentCustomBlocks.remove(customBlock.location);
          Craftory.tickManager.removeTickingBlock(customBlock);
        }
      });
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
        CustomBlock customBlock = customBlockManager.getCustomBlock(e.getClickedBlock().getLocation());
        CustomBlockInteractEvent customBlockInteractEvent = new CustomBlockInteractEvent(
            e.getAction(),
            e.getClickedBlock(),
            e.getBlockFace(),
            e.getItem(),
            e.getPlayer(),
            customBlock);
        Bukkit.getServer().getPluginManager().callEvent(customBlockInteractEvent);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
          e.setCancelled(true);
        }
      }
    }
    return;
  }
}
