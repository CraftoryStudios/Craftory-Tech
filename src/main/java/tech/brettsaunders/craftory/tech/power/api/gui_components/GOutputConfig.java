/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.gui_components;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GOutputConfig implements IGUIComponent, Listener {

  private static ItemStack disabled = null;
  private static ItemStack output = null;
  private final int northSlot;
  private final int eastSlot;
  private final int southSlot;
  private final int westSlot;
  private final int upSlot;
  private final int downSlot;
  private final boolean alt;
  private final Inventory inventory;
  private final Map<BlockFace, Boolean> config;

  public GOutputConfig(Inventory inventory, Map<BlockFace, Boolean> config) {
    this(inventory, config, 43);
  }

  public GOutputConfig(Inventory inventory, Map<BlockFace, Boolean> config, int middleSlot) {
    this(inventory, config, middleSlot, false);
  }

  public GOutputConfig(Inventory inventory, Map<BlockFace, Boolean> config, boolean alt) {
    this(inventory, config, 43, alt);
  }

  public GOutputConfig(Inventory inventory, Map<BlockFace, Boolean> config, int middleSlot,
      boolean alt) {
    this.inventory = inventory;
    this.config = config;
    eastSlot = middleSlot - 1;
    upSlot = middleSlot - 9;
    downSlot = middleSlot + 9;
    southSlot = middleSlot + 8;
    westSlot = middleSlot + 1;
    northSlot = middleSlot;
    this.alt = alt;
    Events.registerEvents(this);

    disabled = CustomItemManager
        .getCustomItem("output_disabled" + (this.alt ? "_alt" : ""));
    output = CustomItemManager
        .getCustomItem("output_green" + (this.alt ? "_alt" : ""));
  }

  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != inventory) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    int rawSlot = event.getRawSlot();
    if (rawSlot == northSlot) {
      config.put(BlockFace.NORTH, !config.get(BlockFace.NORTH));
    } else if (rawSlot == eastSlot) {
      config.put(BlockFace.EAST, !config.get(BlockFace.EAST));
    } else if (rawSlot == southSlot) {
      config.put(BlockFace.SOUTH, !config.get(BlockFace.SOUTH));
    } else if (rawSlot == westSlot) {
      config.put(BlockFace.WEST, !config.get(BlockFace.WEST));
    } else if (rawSlot == upSlot) {
      config.put(BlockFace.UP, !config.get(BlockFace.UP));
    } else if (rawSlot == downSlot) {
      config.put(BlockFace.DOWN, !config.get(BlockFace.DOWN));
    }
  }

  @Override
  public void update() {
    //NORTH, EAST, SOUTH, WEST, UP, DOWN
    inventory.setItem(northSlot, !config.get(BlockFace.NORTH) ? disabled.clone() : output.clone());
    inventory.setItem(southSlot, !config.get(BlockFace.SOUTH) ? disabled.clone() : output.clone());
    inventory.setItem(eastSlot, !config.get(BlockFace.EAST) ? disabled.clone() : output.clone());
    inventory.setItem(westSlot, !config.get(BlockFace.WEST) ? disabled.clone() : output.clone());
    inventory.setItem(upSlot, !config.get(BlockFace.UP) ? disabled.clone() : output.clone());
    inventory.setItem(downSlot, !config.get(BlockFace.DOWN) ? disabled.clone() : output.clone());
  }
}
