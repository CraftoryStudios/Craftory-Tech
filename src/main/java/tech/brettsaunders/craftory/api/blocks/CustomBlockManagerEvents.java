package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Craftory.customBlockManager;
import static tech.brettsaunders.craftory.Utilities.getChunkWorldID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
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
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.core.block.powerGrid.PowerConnector;

public class CustomBlockManagerEvents implements Listener {

  private PersistenceStorage persistenceStorage;
  private HashMap<Location, CustomBlock> currentCustomBlocks;
  private HashMap<String, HashSet<CustomBlock>> activeChunks;
  private HashMap<String, HashSet<CustomBlock>> inactiveChunks;
  private HashMap<String, CustomBlockData> customBlockDataHashMap;
  private StatsContainer statsContainer;

  public CustomBlockManagerEvents(PersistenceStorage persistenceStorage,
      HashMap<Location, CustomBlock> currentCustomBlocks,
      HashMap<String, HashSet<CustomBlock>> activeChunks,
      HashMap<String, HashSet<CustomBlock>> inactiveChunks,
      HashMap<String, CustomBlockData> customBlockDataHashMap,
      StatsContainer statsContainer) {
    this.persistenceStorage = persistenceStorage;
    this.currentCustomBlocks = currentCustomBlocks;
    this.activeChunks = activeChunks;
    this.inactiveChunks = inactiveChunks;
    this.customBlockDataHashMap = customBlockDataHashMap;
    this.statsContainer = statsContainer;
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
      //If Basic Block
      if (Utilities.getBasicBlockRegistry().containsKey(customBlockItemName)) {
        customBlockManager.getCustomBasicBlockOfItem(customBlockItemName, e.getBlockPlaced());
      } else {
        CustomBlock customBlock = customBlockManager
            .getCustomBlockOfItem(customBlockItemName, e.getBlockPlaced());
        CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
            e.getBlockPlaced().getLocation(), customBlockItemName, e.getBlockPlaced(), customBlock);
        Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);
      }
    }
  }

  private void calculateStatsDecrease(CustomBlock customBlock) {
    statsContainer.decreaseTotalCustomBlocks();
    if (customBlock instanceof PoweredBlock) {
      statsContainer.decreaseTotalPoweredBlocks();
      if (customBlock instanceof BaseMachine) {
        statsContainer.decreaseTotalMachines();
      } else if (customBlock instanceof BaseCell) {
        statsContainer.decreaseTotalCells();
      } else if (customBlock instanceof BaseGenerator) {
        statsContainer.decreaseTotalGenerators();
      } else if (customBlock instanceof PowerConnector) {
        statsContainer.decreaseTotalPowerConnectors();
      }
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
      calculateStatsDecrease(customBlock);
      //If Basic Block
    } else if (e.getBlock().getType() == Material.BROWN_MUSHROOM_BLOCK) {
      BlockData blockData = e.getBlock().getBlockData();
      MultipleFacing multipleFacing = (MultipleFacing) blockData;
      Utilities.getBasicBlockRegistry().forEach((name,placement) -> {
        Set<BlockFace> blockFaces = multipleFacing.getFaces();
        Set<BlockFace> compareFaces = placement.getAllowedFaces();
        if (blockFaces.containsAll(compareFaces) && compareFaces.containsAll(blockFaces)) {
          if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            location.getWorld()
                .dropItemNaturally(location, CustomItemManager.getCustomItem(name));
          }
          e.getBlock().setType(Material.AIR);
          return;
        }
      });
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent e) {
    String chunkID = getChunkWorldID(e.getChunk());
    if (inactiveChunks.containsKey(chunkID)) {
      HashSet<CustomBlock> customBlocks = inactiveChunks.get(chunkID);
      customBlocks.forEach(block -> {
        customBlockManager.putActiveCustomBlock(block);
        Craftory.tickManager.addTickingBlock(block);
      });
      inactiveChunks.remove(chunkID);

      //Update Cache
      customBlocks.forEach(customBlock -> {
        if (customBlock instanceof PoweredBlock) {
          ((PoweredBlock) customBlock).refreshSideCache();
        }
      });
    }
  }

  @EventHandler
  public void onChunkUnLoad(ChunkUnloadEvent e) {
    final String chunkID = getChunkWorldID(e.getChunk());
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
            customBlock,
            e);
        Bukkit.getServer().getPluginManager().callEvent(customBlockInteractEvent);
        //if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
          //e.setCancelled(true);
        //}
      }
    }
    return;
  }
}
