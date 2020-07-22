/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver {
  /* Per Object Variables Saved */
  @Persistent
  protected int maxReceive;

  /* Per Object Variables Not-Saved */
  protected transient VariableContainer<Boolean> runningContainer;
  protected transient VariableContainer<Double> progressContainer;
  protected transient int processTime;
  protected transient int energyConsumption;
  protected transient int tickCount = 0;

  /* Construction */
  public BaseMachine(Location location, String blockName, byte level, int maxReceive) {
    super(location, blockName, level);
    this.maxReceive = maxReceive;
    energyStorage.setMaxReceive(maxReceive);
    init();
  }

  /* Saving, Setup and Loading */
  public BaseMachine() {
    super();
    init();
  }

  /* Common Load and Construction */
  private void init() {
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
  }


  /* Update Loop */
  @Ticking(ticks = 1)
  public void updateMachine() {
    if (inventoryInterface == null || isBlockPowered()) {
      return;
    }
    updateSlots();
    if (validateContentes() && hasSufficientEnergy()) {
      runningContainer.setT(true);
      tickCount += 1;
      if (tickCount >= processTime) {
        tickCount = 0;
        processComplete();
      }
    } else {
      runningContainer.setT(false);
      tickCount = 0;
    }
    progressContainer.setT(((double) tickCount) / processTime);
  }

  protected boolean hasSufficientEnergy() {
    if(energyStorage.getEnergyStored() >= energyConsumption){
      energyStorage.modifyEnergyStored(-energyConsumption);
      return true;
    }
    return false;
  }

  protected abstract void processComplete();

  protected abstract boolean validateContentes();

  protected abstract void updateSlots();

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, this.maxReceive), simulate);
  }

  /* IEnergyHandler */
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
    return Math
        .min(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(), maxReceive);
  }

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int maxReceiveEnergy() {
    return maxReceive;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.CELL_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
  }
}
