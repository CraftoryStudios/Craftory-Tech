package tech.brettsaunders.craftory.api.blocks;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGrid;

public class PoweredBlockEvents implements Listener {
  private final HashMap<UUID, HashMap<BlockFace,Boolean>> configuratorData = new HashMap<>();

  public PoweredBlockEvents() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onPoweredBlockPlace(CustomBlockPlaceEvent e) {
    if (PoweredBlockUtils.isEnergyReceiver(e.getCustomBlock())) {
      PoweredBlockUtils.updateAdjacentProviders(e.getLocation(),true, e.getCustomBlock());
    }
    if (e.getName().equals(Blocks.POWER_CONNECTOR)) {
      PowerGrid powerGrid = new PowerGrid();
      powerGrid.addPowerConnector(e.getLocation());
      Craftory.powerGridManager.getAdjacentPowerBlocks(e.getLocation(), powerGrid);
      Craftory.powerGridManager.addPowerGrid(e.getLocation(), powerGrid);
    }
  }

  @EventHandler
  public void onPoweredBlockBreak(CustomBlockBreakEvent e) {
    //Update Providers
    if (PoweredBlockUtils.isEnergyReceiver(e.getCustomBlock())) {
      PoweredBlockUtils.updateAdjacentProviders(e.getLocation(), false, e.getCustomBlock());
    }
    //Drop Inventory
    if(PoweredBlockUtils.isPoweredBlock(e.getCustomBlock())) {
      PoweredBlock poweredBlock = (PoweredBlock) e.getCustomBlock();
      World world = e.getLocation().getWorld();
      Inventory inventory = poweredBlock.getInventory();
      if (inventory != null) {
        ItemStack item;
        for (Integer i : poweredBlock.getInteractableSlots()) {
          item = inventory.getItem(i);
          if (item != null) {
            world.dropItemNaturally(e.getLocation(), item);
          }
        }
      }
    }
  }

  @EventHandler
  public void onWrenchClick(CustomBlockInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    if (!CustomItemManager.matchCustomItemName(e.getItemStack(), CoreHolder.Items.WRENCH)) {
      return;
    }
    e.setCancelled(true);
    //Show power levels
    if (PoweredBlockUtils.isPoweredBlock(e.getCustomBlock())) {
      PoweredBlock block = (PoweredBlock) e.getCustomBlock();
      e.getPlayer().sendMessage(
          Utilities.langProperties.getProperty("EnergyStored")+": " + block.getInfoEnergyStored() + " RE / " + block.getInfoEnergyCapacity()
              + " RE");
    }
  }

  @EventHandler
  public void onConfiguratorClick(CustomBlockInteractEvent e) {
    if (!CustomItemManager.matchCustomItemName(e.getItemStack(), CoreHolder.Items.CONFIGURATOR)) {
      return;
    }
    e.setCancelled(true);

    final Player player = e.getPlayer();
    if (e.getAction() == Action.RIGHT_CLICK_AIR && player.isSneaking()) {
      configuratorData.remove(player.getUniqueId());
      player.sendMessage(Utilities.langProperties.getProperty("SideConfigClear"));
    }
    if (PoweredBlockUtils.isEnergyProvider(e.getCustomBlock())) {
      BaseProvider provider = (BaseProvider) e.getCustomBlock();
      if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
        configuratorData.put(player.getUniqueId(), provider.getSideConfig());
        player.sendMessage(Utilities.langProperties.getProperty("SideConfigCopied"));
      } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        if (configuratorData.containsKey(player.getUniqueId())) {
          provider.setSidesConfig(configuratorData.get(player.getUniqueId()));
          player.sendMessage(Utilities.langProperties.getProperty("SideConfigPasted"));
        } else {
          player.sendMessage(Utilities.langProperties.getProperty("SideConfigNoData"));
        }
      }
    }
  }

  @EventHandler
  public void onGUIBlockClick(CustomBlockInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if ((e.getPlayer().isSneaking() || CustomItemManager
        .matchCustomItemName(e.getItemStack(), CoreHolder.Items.CONFIGURATOR))) {
      return;
    }

    if (PoweredBlockUtils.isPoweredBlock(e.getCustomBlock())) {
      PoweredBlock poweredBlock = (PoweredBlock) e.getCustomBlock();
      //Open GUI of Powered Block
      poweredBlock.openGUI(e.getPlayer());
      e.getBaseEvent().setCancelled(true);
    }
  }

  @EventHandler
  public void onVanillaBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (block.getType().equals(Material.HOPPER)) {
      PoweredBlockUtils.updateHopperNeighbour(block, false);
    }
  }

  @EventHandler
  public void onVanillaBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlockPlaced();
    if (block.getType().equals(Material.HOPPER)) {
      PoweredBlockUtils.updateHopperNeighbour(block, true);
    }
  }

}
