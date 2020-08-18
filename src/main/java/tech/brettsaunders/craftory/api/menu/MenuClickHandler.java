/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface MenuClickHandler {

  boolean onClick(Player p, int slot, ItemStack item, ItemStack cursor, ClickAction action);
}
