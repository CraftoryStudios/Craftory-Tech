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

import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseGenerator extends BaseProvider implements IHopperInteract {


  /* Per Object Variables Saved */
  protected static Map<BlockFace, Integer> inputFaces = new EnumMap<>(BlockFace.class);
  protected static Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  /* Per Object Variables Not-Saved */
  protected  VariableContainer<Boolean> runningContainer;
  @Persistent
  protected int energyProduced;
  protected boolean isActive;

  /* Construction */
  protected BaseGenerator(Location location, String blockName, byte level, int outputAmount,
      int capacity) {
    super(location, blockName, level, outputAmount);
    energyStorage = new EnergyStorage(capacity);
    setup();
  }

  /* Saving, Setup and Loading */
  protected BaseGenerator() {
    super();
    isActive = false;
    setup();
  }

  /* Common Load and Construction */
  private void setup() {
    isActive = false;
    runningContainer = new VariableContainer<>(false);
  }

  /* Update Loop */
  @Ticking(ticks = 1)
  public void updateGenerator() {
    if (isBlockPowered()) {
      return;
    }
    if (isActive) {
      processTick();
      if (canFinish()) {
        if (!canStart()) {
          processOff();
        } else {
          processStart();
        }
      }
    } else {
      if (canStart()) {
        processStart();
        processTick();
        isActive = true;
      }
    }
    if (!isActive) {
      processIdle();
    }
  }

  protected abstract boolean canStart();

  protected abstract boolean canFinish();

  protected void processStart() {
    runningContainer.setT(true);
  }

  protected void processIdle() {
  }

  protected void processOff() {
    isActive = false;
    runningContainer.setT(false);
  }

  protected abstract void processTick();

  /* External Methods */
  public final void setEnergyStored(int quantity) {
    energyStorage.setEnergyStored(quantity);
  }

  /* IEnergyInfo */
  @Override
  public int getInfoEnergyPerTick() {
    if (!isActive) {
      return 0;
    }
    return energyProduced;
  }

  @Override
  public int getInfoEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.GENERATOR_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 31));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, true));
    this.inventoryInterface = inventory;
  }


  @Override
  public Map<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }

  @Override
  public Map<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }

}
