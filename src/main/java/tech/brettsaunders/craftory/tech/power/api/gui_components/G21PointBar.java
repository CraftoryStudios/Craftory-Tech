/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.gui_components;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class G21PointBar implements IGUIComponent {

  private final int topSlot;
  private final int bottomSlot;
  private final Inventory inventory;

  protected G21PointBar(Inventory inventory, int topSlot) {
    this.inventory = inventory;
    this.topSlot = topSlot;
    bottomSlot = topSlot + 27;
  }

  protected G21PointBar(Inventory inventory) {
    this(inventory, 10);
  }

  @Override
  public void update() {
    setLevelIndicator();
  }

  abstract String getDisplayName();

  abstract double getAmountFilled();

  abstract String getItemName();

  private void setLevelIndicator() {
    //Percentage of capacity filled
    double amountFilled = getAmountFilled();
    //Calculate amount of power bars to display
    int bottom = 0;
    int top = 0;
    if (amountFilled != 0) {
      if (amountFilled > 50) {
        top = (int) Math.round((amountFilled - 50) * 0.4);
        bottom = 20;
      } else {
        bottom = (int) Math.round(amountFilled * 0.4);
      }
    }
    String displayName = getDisplayName();
    //Get Top Battery Icon and set Display Name
    String topTexture = getItemName() + "_" + top + "_t";
    ItemStack topItem = CustomItemManager.getCustomItem(topTexture);
    ItemMeta topMeta = topItem.getItemMeta();
    topMeta.setDisplayName(displayName);
    topItem.setItemMeta(topMeta);

    //Get Bottom Battery Icon and set Display Name
    String bottomTexture = getItemName() + "_" + bottom + "_b";
    ItemStack bottomItem = CustomItemManager.getCustomItem(bottomTexture);
    ItemMeta bottomMeta = bottomItem.getItemMeta();
    bottomMeta.setDisplayName(displayName);
    bottomItem.setItemMeta(bottomMeta);

    //Fill other battery slots
    ItemStack batteryIndicator = CustomItemManager.getCustomItem("invisible");
    ItemMeta batteryIndicatorMeta = batteryIndicator.getItemMeta();
    batteryIndicatorMeta.setDisplayName(displayName);
    batteryIndicator.setItemMeta(batteryIndicatorMeta);

    //Display in Inventory
    inventory.setItem(topSlot, topItem);
    inventory.setItem(bottomSlot, bottomItem);

    //Fill other slots
    for (int i = -1; i < 1; i++) {
      int x = topSlot + i;
      for (int j = -1; j < 5; j++) {
        int slot = x + (9 * j);
        if (slot > -1 && slot < 54 && slot != topSlot && slot != bottomSlot) {
          inventory.setItem(slot, batteryIndicator);
        }
      }
    }
  }
}
