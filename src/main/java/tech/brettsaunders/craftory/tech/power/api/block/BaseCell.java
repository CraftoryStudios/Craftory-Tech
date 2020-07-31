/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver {

  protected static final int CAPACITY_BASE = 400000;
  protected static final int[] CAPACITY_LEVEL = {1, 5, 50, 200};
  protected static final int MAX_INPUT = 200;
  protected static final int[] INPUT_LEVEL = {1, 4, 40, 160};
  /* Static Constants */

  /* Construction */
  public BaseCell(Location location, String blockName, byte level, int outputAmount) {
    super(location, blockName, level, outputAmount);
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
  }

  /* Saving, Setup and Loading */
  public BaseCell() {
    super();
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage
        .receiveEnergy(Math.min(maxReceive, MAX_INPUT * INPUT_LEVEL[level]), simulate);
  }

  @Override
  public int getEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public int getEnergySpace() {
    return Math.max(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(),
        MAX_INPUT * INPUT_LEVEL[level]);
  }

  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.CELL_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, 23, true));
  }
}
