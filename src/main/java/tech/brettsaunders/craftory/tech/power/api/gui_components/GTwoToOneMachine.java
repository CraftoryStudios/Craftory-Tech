/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.gui_components;

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

  public GTwoToOneMachine(Inventory inventory, int arrowSlot,
      VariableContainer<Double> progress) {
    this.inventory = inventory;
    this.arrowSlot = arrowSlot;
    this.progress = progress;
  }

  public GTwoToOneMachine(Inventory inventory,
      VariableContainer<Double> progress) {
    this.inventory = inventory;
    this.progress = progress;
    this.arrowSlot = 24;
  }


  @Override
  public void update() {
    int x = (int) Math.floor(progress.getT() * 10);
    ItemStack arrow = CustomItemManager.getCustomItem("arrow_" + x);
    ItemMeta meta = arrow.getItemMeta();
    meta.setDisplayName(ChatColor.RESET + "");
    arrow.setItemMeta(meta);
    inventory.setItem(arrowSlot, arrow);
  }
}
