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

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GOneToOneMachine implements IGUIComponent {

  private final int slot;
  private final VariableContainer<Double> progress;
  private final Inventory inventory;
  private final int inputSlot;
  private final int outputSlot;

  public GOneToOneMachine(Inventory inventory, int slot,
      VariableContainer<Double> progress, int inputSlot, int outputSlot) {
    this.inventory = inventory;
    this.slot = slot;
    this.progress = progress;
    this.inputSlot = inputSlot;
    this.outputSlot = outputSlot;
  }

  public GOneToOneMachine(Inventory inventory,
      VariableContainer<Double> progress, int inputSlot, int outputSlot) {
    this.inventory = inventory;
    this.progress = progress;
    this.outputSlot = outputSlot;
    this.inputSlot = inputSlot;
    this.slot = 24;
  }

  @Override
  public void update() {
    int x = (int) Math.floor(progress.getT() * 10);
    ItemStack arrow = CustomItemManager.getCustomItem("arrow_" + x);
    ItemMeta meta = arrow.getItemMeta();
    meta.setDisplayName(ChatColor.RESET + "");
    arrow.setItemMeta(meta);
    inventory.setItem(slot, arrow);
  }
}
