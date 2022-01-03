/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.gui_components;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Constants.FLUIDS;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.fluids.FluidStorage;

public class GTank extends G21PointBar {

  private final FluidStorage storage;
  private static final FLUIDS fluid = FLUIDS.LAVA;

  public GTank(Inventory inventory, FluidStorage storage, int topSlot) {
    super(inventory, topSlot);
    this.storage = storage;
  }

  public GTank(Inventory inventory, FluidStorage storage) {
    this(inventory, storage, 12);
  }

  @Override
  BaseComponent getDisplayName() {
    TranslatableComponent name = new TranslatableComponent(fluid.toString());
    name.addWith(new TranslatableComponent("Stored"));
    name.addWith(": " + Utilities.rawFluidToPrefixed(storage.getFluidStored()));
    return name;
  }

  @Override
  double getAmountFilled() {
    return ((double) storage.getFluidStored() / (double) storage.getMaxFluidStored())
        * (double) 100;
  }

  @Override
  String getItemName() {

    return fluid.toString();
  }
}
