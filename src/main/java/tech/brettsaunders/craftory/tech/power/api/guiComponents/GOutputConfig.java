/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GOutputConfig implements IGUIComponent, Listener {

  private static ItemStack DISABLED = null;
  private static ItemStack OUTPUT = null;
  private final int NORTH_SLOT;
  private final int EAST_SLOT;
  private final int SOUTH_SLOT;
  private final int WEST_SLOT;
  private final int UP_SLOT;
  private final int DOWN_SLOT;
  private final boolean ALT;
  private final Inventory inventory;
  private final HashMap<BlockFace, Boolean> config;

  public GOutputConfig(Inventory inventory, HashMap<BlockFace, Boolean> config) {
    this(inventory, config, 43);
  }

  public GOutputConfig(Inventory inventory, HashMap<BlockFace, Boolean> config, int middleSlot) {
    this(inventory, config, middleSlot, false);
  }

  public GOutputConfig(Inventory inventory, HashMap<BlockFace, Boolean> config, boolean alt) {
    this(inventory, config, 43, alt);
  }

  public GOutputConfig(Inventory inventory, HashMap<BlockFace, Boolean> config, int middleSlot,
      boolean alt) {
    this.inventory = inventory;
    this.config = config;
    EAST_SLOT = middleSlot - 1;
    UP_SLOT = middleSlot - 9;
    DOWN_SLOT = middleSlot + 9;
    SOUTH_SLOT = middleSlot + 8;
    WEST_SLOT = middleSlot + 1;
    NORTH_SLOT = middleSlot;
    ALT = alt;
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);

    DISABLED = CustomItemManager
        .getCustomItem("output_disabled" + (ALT ? "_alt" : ""));
    OUTPUT = CustomItemManager
        .getCustomItem("output_green" + (ALT ? "_alt" : ""));
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
    if (rawSlot == NORTH_SLOT) {
      config.put(BlockFace.NORTH, !config.get(BlockFace.NORTH));
    } else if (rawSlot == EAST_SLOT) {
      config.put(BlockFace.EAST, !config.get(BlockFace.EAST));
    } else if (rawSlot == SOUTH_SLOT) {
      config.put(BlockFace.SOUTH, !config.get(BlockFace.SOUTH));
    } else if (rawSlot == WEST_SLOT) {
      config.put(BlockFace.WEST, !config.get(BlockFace.WEST));
    } else if (rawSlot == UP_SLOT) {
      config.put(BlockFace.UP, !config.get(BlockFace.UP));
    } else if (rawSlot == DOWN_SLOT) {
      config.put(BlockFace.DOWN, !config.get(BlockFace.DOWN));
    }
  }

  @Override
  public void update() {
    //NORTH, EAST, SOUTH, WEST, UP, DOWN
    inventory.setItem(NORTH_SLOT, !config.get(BlockFace.NORTH) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(SOUTH_SLOT, !config.get(BlockFace.SOUTH) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(EAST_SLOT, !config.get(BlockFace.EAST) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(WEST_SLOT, !config.get(BlockFace.WEST) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(UP_SLOT, !config.get(BlockFace.UP) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(DOWN_SLOT, !config.get(BlockFace.DOWN) ? DISABLED.clone() : OUTPUT.clone());
  }
}
