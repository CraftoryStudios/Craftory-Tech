/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.gui_components;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

public class GBattery extends G21PointBar {


  private final EnergyStorage storage;

  public GBattery(Inventory inventory, EnergyStorage storage, int topSlot) {
    super(inventory, topSlot);
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
