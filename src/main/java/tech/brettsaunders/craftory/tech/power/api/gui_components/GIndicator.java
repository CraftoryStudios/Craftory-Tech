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

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GIndicator implements IGUIComponent {

  private final int slot;
  private final VariableContainer<Boolean> state;
  private final Inventory inventory;

  public GIndicator(Inventory inventory, VariableContainer<Boolean> state) {
    this(inventory, state, 52);
  }

  public GIndicator(Inventory inventory, VariableContainer<Boolean> state, int slot) {
    this.inventory = inventory;
    this.state = state;
    this.slot = slot;
  }

  @Override
  public void update() {
    ItemStack light;
    String name;

    if (Boolean.TRUE.equals(state.getT())) {
      light = CustomItemManager.getCustomItem("light_on");
      name = Utilities.getTranslation("light_on");
    } else {
      light = CustomItemManager.getCustomItem("light_off");
      name = Utilities.getTranslation("light_off");
    }
    ItemMeta meta = light.getItemMeta();
    meta.setDisplayName(ChatColor.RESET + name);
    light.setItemMeta(meta);
    inventory.setItem(slot, light);
  }
}
