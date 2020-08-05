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
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

public class GBattery extends G21PointBar {


  private final EnergyStorage storage;

  public GBattery(Inventory inventory, EnergyStorage storage, int top_slot) {
    super(inventory, top_slot);
    this.storage = storage;
  }

  public GBattery(Inventory inventory, EnergyStorage storage) {
    this(inventory, storage, 10);
  }

  @Override
  String getDisplayName() {
    return ChatColor.RESET + Utilities.getTranslation("EnergyStored") + ": " + Utilities
        .rawEnergyToPrefixed(storage.getEnergyStored());
  }

  @Override
  double getAmountFilled() {
    return ((double) storage.getEnergyStored() / (double) storage.getMaxEnergyStored())
        * (double) 100;
  }

  @Override
  String getItemName() {
    return "bar";
  }


}
