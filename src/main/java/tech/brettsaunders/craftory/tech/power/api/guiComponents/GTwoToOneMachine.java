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

public class GTwoToOneMachine implements IGUIComponent {


  private final int arrowSlot;
  private final VariableContainer<Double> progress;
  private final Inventory inventory;
  private final int inputSlot1;
  private final int inputSlot2;
  private final int outputSlot;

  public GTwoToOneMachine(Inventory inventory, int arrowSlot,
      VariableContainer<Double> progress, int inputSlot1, int inputSlot2, int outputSlot) {
    this.inventory = inventory;
    this.arrowSlot = arrowSlot;
    this.progress = progress;
    this.inputSlot1 = inputSlot1;
    this.inputSlot2 = inputSlot2;
    this.outputSlot = outputSlot;
  }

  public GTwoToOneMachine(Inventory inventory,
      VariableContainer<Double> progress, int inputSlot1, int inputSlot2, int outputSlot) {
    this.inventory = inventory;
    this.progress = progress;
    this.outputSlot = outputSlot;
    this.inputSlot1 = inputSlot1;
    this.inputSlot2 = inputSlot2;
    this.arrowSlot = 24;
  }



  @Override
  public void update() {
    int x = (int) Math.floor(progress.getT() * 10);
    ItemStack arrow = CustomItemManager.getCustomItem("arrow_" + x);
    ItemMeta meta = arrow.getItemMeta();
    meta.setDisplayName(ChatColor.RESET +"");
    arrow.setItemMeta(meta);
    inventory.setItem(arrowSlot, arrow);
  }
}
