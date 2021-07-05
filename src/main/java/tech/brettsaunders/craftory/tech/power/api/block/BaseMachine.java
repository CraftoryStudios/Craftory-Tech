/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Constants.Sounds;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.utils.Light;
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver {

  /* Per Object Variables Saved */
  @Persistent
  protected int maxReceive;

  /* Per Object Variables Not-Saved */
  protected  VariableContainer<Boolean> runningContainer;
  protected  VariableContainer<Double> progressContainer;
  protected  int processTime;
  protected  int energyConsumption;
  protected  int tickCount = 0;
  protected boolean lightSpawned = false;
  protected static int lightLevel = 10;

  /* Construction */
  protected BaseMachine(Location location, String blockName, byte level, int maxReceive) {
    super(location, blockName, level);
    this.maxReceive = maxReceive;
    energyStorage.setMaxReceive(maxReceive);
    init();
  }

  /* Saving, Setup and Loading */
  protected BaseMachine() {
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
      if(!lightSpawned){
        lightSpawned = true;
        Light.createLight(location, lightLevel, false);
      }
    } else {
      runningContainer.setT(false);
      tickCount = 0;
      if(lightSpawned) {
        lightSpawned = false;
        Light.deleteLight(location, false);
      }
    }
    progressContainer.setT(((double) tickCount) / processTime);
  }

  @Ticking(ticks = 1200)
  public void  refreshLight() {
    if(!Craftory.plugin.isPluginLoaded("LightAPI")) {
      return;
    }
    if(lightSpawned) {
      Light.deleteLight(location, false);
      Light.createLight(location, lightLevel, false);
    }
  }

  protected boolean hasSufficientEnergy() {
    if (energyStorage.getEnergyStored() >= energyConsumption) {
      energyStorage.modifyEnergyStored(-energyConsumption);
      return true;
    }
    return false;
  }

  @Ticking(ticks=60)
  public void soundLoop() {
    if(Boolean.TRUE.equals(runningContainer.getT())) {
      playSound();
    }
  }

  protected void playSound() {
    location.getWorld().playSound(location, Sounds.MACHINE_1, SoundCategory.BLOCKS, 1, 1);
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
