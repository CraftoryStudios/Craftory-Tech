/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;

public class SolidFuelGenerator extends BaseGenerator {
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 80;

  public static final int FUEL_SLOT = 22;
  /* Static Constants Protected */
  protected static final int CAPACITY_BASE = 40000;
  protected static final double[] CAPACITY_LEVEL = {1, 1.5, 2, 3};

  //TODO Remove in future
  @Deprecated
  @Setter
  protected ItemStack fuelItem = new ItemStack(Material.AIR);
  @Persistent
  protected int maxFuelRE;
  @Persistent
  protected int fuelRE;

  /* Construction */
  public SolidFuelGenerator() {
    super();
    init();
  }

  /* Saving, Setup and Loading */
  public SolidFuelGenerator(Location location) {
    super(location, Blocks.SOLID_FUEL_GENERATOR, C_LEVEL, C_OUTPUT_AMOUNT,(int) (CAPACITY_BASE * CAPACITY_LEVEL[0]));
    init();
    inputSlots = new ArrayList<>();
    inputSlots.add(0,new ItemStack(Material.AIR));
  }

  private void init() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0,FUEL_SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(FUEL_SLOT));
    inputFaces = new HashMap<BlockFace, Integer>() {
      {
        put(BlockFace.NORTH, FUEL_SLOT);
        put(BlockFace.EAST, FUEL_SLOT);
        put(BlockFace.SOUTH, FUEL_SLOT);
        put(BlockFace.WEST, FUEL_SLOT);
        put(BlockFace.UP, FUEL_SLOT);
      }
    };
  }

  protected boolean canFinish() {
    return fuelRE <= 0;
  }

  @Override
  protected boolean canStart() {
    if (getFuelItem() == null) {
      return false;
    }
    return getEnergySpace() > 0
        && SolidFuelManager.getFuelEnergy(getFuelItem().getType().name()) > 0;
  }

  @Override
  protected void processTick() {
    energyProduced = getMaxOutput();
    energyStorage.modifyEnergyStored(energyProduced);
    fuelRE -= energyProduced;
  }

  @Override
  protected void processStart() {
    super.processStart();
    maxFuelRE = SolidFuelManager.getFuelEnergy(getFuelItem().getType().name());
    fuelRE += maxFuelRE;
    consumeFuel();
  }

  protected ItemStack getFuelItem() {
    if (getInventory() == null) {
      return null;
    }
    return getInventory().getItem(FUEL_SLOT);
  }

  protected void consumeFuel() {
    ItemStack fuel = getFuelItem();
    if (fuel.getAmount() > 1) {
      fuel.setAmount(fuel.getAmount() - 1);
      getInventory().setItem(FUEL_SLOT, fuel);
    } else {
      getInventory().clear(FUEL_SLOT);
    }
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.GENERATOR_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, 43, true));
    addGUIComponent(new GIndicator(inventory, runningContainer, 31));
    this.inventoryInterface = inventory;
  }

  //TODO Remove in future version
  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    if (fuelItem.getType() != Material.AIR && fuelItem != null) {
      inventoryInterface.setItem(FUEL_SLOT, fuelItem);
    }
  }

}
