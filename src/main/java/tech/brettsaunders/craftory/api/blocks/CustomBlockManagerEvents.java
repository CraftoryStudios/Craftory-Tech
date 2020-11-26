/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Craftory.customBlockManager;
import static tech.brettsaunders.craftory.Craftory.lastVersionCode;
import static tech.brettsaunders.craftory.Utilities.getChunkWorldID;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.blocks.tools.ToolLevel;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;

public class CustomBlockManagerEvents implements Listener {

  private final PersistenceStorage persistenceStorage;
  private final Map<Location, CustomBlock> currentCustomBlocks;
  private final Map<String, HashSet<CustomBlock>> activeChunks;
  private final Map<String, HashSet<CustomBlock>> inactiveChunks;
  private final Map<String, CustomBlockData> customBlockDataHashMap;

  public CustomBlockManagerEvents(PersistenceStorage persistenceStorage,
      Map<Location, CustomBlock> currentCustomBlocks,
      Map<String, HashSet<CustomBlock>> activeChunks,
      Map<String, HashSet<CustomBlock>> inactiveChunks,
      Map<String, CustomBlockData> customBlockDataHashMap) {
    this.persistenceStorage = persistenceStorage;
    this.currentCustomBlocks = currentCustomBlocks;
    this.activeChunks = activeChunks;
    this.inactiveChunks = inactiveChunks;
    this.customBlockDataHashMap = customBlockDataHashMap;
    Events.registerEvents(this);
  }

  @EventHandler
  public void onWorldInit(WorldInitEvent e) {
    CustomBlockStorage
        .loadAllSavedRegions(e.getWorld(), CustomBlockManager.DATA_FOLDER, customBlockManager,
            persistenceStorage);
    //Load Custom Block Data into memory of pre-loaded chunks
    for (Chunk chunk : e.getWorld().getLoadedChunks()) {
      loadCustomBlocksChunk(chunk);
    }
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    if (!CustomItemManager.isCustomBlockItem(e.getItemInHand())) {
      return;
    }
    NBTItem nbtItem = new NBTItem(e.getItemInHand());
    final String customBlockItemName = CustomItemManager.getCustomItemName(nbtItem);
    if (!customBlockDataHashMap.containsKey(customBlockItemName)) {
      return;
    }
    if (!e.isCancelled()) {
      //If Basic Block
      if (Utilities.getBasicBlockRegistry().containsKey(customBlockItemName)) {
        customBlockManager.placeBasicCustomBlock(customBlockItemName, e.getBlockPlaced());
      } else {
        CustomBlock customBlock = customBlockManager
            .placeCustomBlock(customBlockItemName, e.getBlockPlaced(), e.getPlayer().getFacing());
        CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
            e.getBlockPlaced().getLocation(), customBlockItemName, e.getBlockPlaced(), customBlock);
        Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);

        //Give data
        if (Boolean.TRUE.equals(nbtItem.hasKey("extraData"))) {
          NBTCompound extraCompound = nbtItem.getCompound("extraData");
          if (extraCompound.hasKey("energyStorage") && customBlock instanceof PoweredBlock) {
            ((PoweredBlock) customBlock).getEnergyStorage().setEnergyStored(extraCompound.getInteger("energyStorage"));
          }
        }

      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onMushroomPhysics(BlockPhysicsEvent event) {
    if ((event.getChangedType() == Material.MUSHROOM_STEM)) {
      event.setCancelled(true);
      event.getBlock().getState().update(true, false);
    }
  }

  /* Item Based Listener */
  @EventHandler
  public void onDurabilityItemUse(PlayerInteractEvent e) {
    if (e.getItem() == null) {
      return;
    }
    Material type = e.getItem().getType();
    if (type != Material.STONE_HOE) {
      return;
    }
    if (!CustomItemManager.isCustomItem(e.getItem(), false)) {
      return;
    }
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    e.setCancelled(true);
  }

  public BlockFace getBlockFace(Player player) {
    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
    Block targetBlock = lastTwoTargetBlocks.get(1);
    Block adjacentBlock = lastTwoTargetBlocks.get(0);
    return targetBlock.getFace(adjacentBlock);
  }

  public Location calculateLocation(Location start, Player player) {
    BlockFace blockFace = getBlockFace(player);
    start.add(start.getX() > 0 ? -0.5 : 0.5, 0.0, start.getZ() > 0 ? -0.5 : 0.5);
    switch (blockFace) {
      default:
      case NORTH:
        start.add(0,0,-0.4);
        break;
      case SOUTH:
        start.add(0,0,0.4);
        break;
      case EAST:
        start.add(0.4,0,0);
        break;
      case WEST:
        start.add(-0.4,0,0);
        break;
    }
    return start;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockDamage(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
    final Location location = e.getClickedBlock().getLocation();
    if (currentCustomBlocks.containsKey(location)) {
      CustomBlock customBlock = currentCustomBlocks.get(location);
      ToolLevel toolLevel = customBlockDataHashMap.get(customBlock.blockName).breakLevel;
      if (toolLevel == ToolLevel.HAND) return;
      Material itemInHand;
      if (e.getItem() == null) {
        itemInHand = Material.AIR;
      } else {
        itemInHand = e.getItem().getType();
      }
      switch (toolLevel) {
        default:
        case IRON:
          if (!(itemInHand == Material.IRON_PICKAXE || itemInHand == Material.GOLDEN_PICKAXE || itemInHand == Material.DIAMOND_PICKAXE || itemInHand == Material.NETHERITE_PICKAXE)) {
            slowBreaking(e.getPlayer(),e.getClickedBlock().getLocation());
          }
          break;
        case GOLD:
          if (!(itemInHand == Material.GOLDEN_PICKAXE || itemInHand == Material.DIAMOND_PICKAXE || itemInHand == Material.NETHERITE_PICKAXE)) {
            slowBreaking(e.getPlayer(),e.getClickedBlock().getLocation());
          }
          break;
        case DIAMOND:
          if (!(itemInHand == Material.DIAMOND_PICKAXE || itemInHand == Material.NETHERITE_PICKAXE)) {
            slowBreaking(e.getPlayer(),e.getClickedBlock().getLocation());
          }
          break;
        case NETHERITE:
          if (itemInHand != Material.NETHERITE_PICKAXE) {
            slowBreaking(e.getPlayer(),e.getClickedBlock().getLocation());
          }
          break;
      }
    }
  }

  private void slowBreaking(Player player, Location location) {
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,
        Integer.MAX_VALUE,1,
        false, false, false));
    BukkitRunnable bukkitRunnable = new BukkitRunnable() {
      @Override
      public void run() {
        Block block = player.getTargetBlockExact(5);
        if (block == null || !block.getLocation().equals(location)){
          player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
          cancel();
        }
      }
    };
    bukkitRunnable.runTaskTimer(Craftory.plugin,4L,4L);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    final Location location = e.getBlock().getLocation();
    if (currentCustomBlocks.containsKey(location)) {
      CustomBlock customBlock = currentCustomBlocks.get(location);
      customBlock.blockBreak();
      CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(
          location, customBlock.blockName, customBlock);
      if (e.isCancelled()) {
        customBlockBreakEvent.setCancelled(true);
      } else {
        customBlockManager.removeCustomBlock(customBlock);
        Craftory.tickManager.removeTickingBlock(customBlock);
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
          location.getWorld()
              .dropItemNaturally(location, CustomItemManager.getCustomItem(customBlock.blockName));
        }
      }
      Bukkit.getPluginManager().callEvent(customBlockBreakEvent);
      e.getBlock().setType(Material.AIR);
      //If Basic Block
    } else if (e.getBlock().getType() == Material.MUSHROOM_STEM) {
      BlockData blockData = e.getBlock().getBlockData();
      MultipleFacing multipleFacing = (MultipleFacing) blockData;
      Utilities.getBasicBlockRegistry().forEach((name, placement) -> {
        Set<BlockFace> blockFaces = multipleFacing.getFaces();
        HashSet<BlockFace> compareFaces = placement.getAllowedFaces();
        if (blockFaces.containsAll(compareFaces) && compareFaces.containsAll(blockFaces)) {
          if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            location.getWorld()
                .dropItemNaturally(location, CustomItemManager.getCustomItem(name));
          }
          e.getBlock().setType(Material.AIR);
          return;
        }
      });
    } else if (e.getBlock().getType() == Material.BROWN_MUSHROOM_BLOCK) {
      BlockData blockData = e.getBlock().getBlockData();
      MultipleFacing multipleFacing = (MultipleFacing) blockData;
      Utilities.getBasicBlockRegistry().forEach((name, placement) -> {
        Set<BlockFace> blockFaces = multipleFacing.getFaces();
        HashSet<BlockFace> compareFaces = placement.getAllowedFaces();
        if (blockFaces.containsAll(compareFaces) && compareFaces.containsAll(blockFaces) && name
            .equalsIgnoreCase(
                Blocks.COPPER_ORE)) {
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
    loadCustomBlocksChunk(e.getChunk());
  }

  private void loadCustomBlocksChunk(Chunk chunk) {
    String chunkID = getChunkWorldID(chunk);
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

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPistonExtend(BlockPistonExtendEvent e) {
    e.getBlocks().forEach((block -> {
      if (currentCustomBlocks.containsKey(block.getLocation())) {
        e.setCancelled(true);
        return;
      }
    }));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
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
    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) && currentCustomBlocks.containsKey(e.getClickedBlock().getLocation())) {
        CustomBlock customBlock = customBlockManager
            .getCustomBlock(e.getClickedBlock().getLocation());
        CustomBlockInteractEvent customBlockInteractEvent = new CustomBlockInteractEvent(
            e.getAction(),
            e.getClickedBlock(),
            e.getBlockFace(),
            e.getItem(),
            e.getPlayer(),
            customBlock,
            e);
        Bukkit.getServer().getPluginManager().callEvent(customBlockInteractEvent);
    }
  }

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent e) {
    if (Utilities.updateItemGraphics) {
      CustomItemManager.updateInventoryItemGraphics(e.getInventory());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    if (Utilities.updateItemGraphics) {
      CustomItemManager.updateInventoryItemGraphics(e.getPlayer().getInventory());
    }
    if (lastVersionCode == 0 && Craftory.folderExists && e.getPlayer().isOp() || e.getPlayer().hasPermission("craftory.give") || e.getPlayer()
        .hasPermission("craftory.debug")) {
        Utilities.msg(e.getPlayer(), "It looks like you are updating from V0.2.0 or lower.");
        Utilities
            .msg(e.getPlayer(), "Due to changes all Items and Blocks may lose their textures.");
        Utilities.msg(e.getPlayer(),
            "To deal with this your server should have converted all blocks to the new format.");
        Utilities.msg(e.getPlayer(),
            "All items will be convert when the player opens an inventory with them in, until you turn off the config option Fix Item Graphics.");
        Utilities.msg(e.getPlayer(),
            "Once turned off you can still convert items with /fixGraphics command!");
    }
  }
}
