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

import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.CHARGE_KEY;
import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.MAX_CHARGE_KEY;
import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.isPoweredTool;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver {

  protected static final int CAPACITY_BASE = 400000;
  protected static final int[] CAPACITY_LEVEL = {1, 5, 50, 200};
  protected static final int MAX_INPUT = 200;
  protected static final int[] INPUT_LEVEL = {1, 4, 40, 160};
  protected static final int CHARGE_SPEED_BASE = 5;
  protected static final int[] CHARGE_SPEED_LEVEL = {1,2,4,8};
  protected static final int ITEM_LOCATION = 50;
  /* Static Constants */

  /* Construction */
  public BaseCell(Location location, String blockName, byte level, int outputAmount) {
    super(location, blockName, level, outputAmount);
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    init();
  }

  /* Saving, Setup and Loading */
  public BaseCell() {
    super();
    init();
  }

  private void init() {
    inputLocations = new ArrayList<>();
    outputLocations = new ArrayList<>();
    inputLocations.add(ITEM_LOCATION);
    interactableSlots = new HashSet<>(Arrays.asList(ITEM_LOCATION));
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

  /* Update Loop */
  @Ticking(ticks = 1)
  public void chargeItem() {
    if(getEnergyStored() < 1) return;
    ItemStack item = inventoryInterface.getItem(ITEM_LOCATION);
    if(item!=null && isPoweredTool(item)){
      NBTItem nbt = new NBTItem(item);
      int charge = nbt.getInteger(CHARGE_KEY);
      int maxCharge = nbt.getInteger(MAX_CHARGE_KEY);
      int diff = maxCharge - charge;
      if(diff <= 0) return;
      int cost = Math.min(diff, CHARGE_SPEED_BASE*CHARGE_SPEED_LEVEL[level]);
      charge += energyStorage.extractEnergy(cost, false);
      item = PoweredToolManager.setCharge(item,charge);
      inventoryInterface.setItem(ITEM_LOCATION, item);
    }
    inputSlots.set(0, item);
  }

  @Override
  public void setupGUI() {
    //TODO change GUI so that it has the charging slot
    inventoryInterface = createInterfaceInventory(displayName, Font.CELL_GUI.label + "");
    addGUIComponent(new GBattery(inventoryInterface, energyStorage));
    addGUIComponent(new GOutputConfig(inventoryInterface, sidesConfig, 23, true));

    if (inputSlots.size() == 0) {
      inputSlots.add(0, new ItemStack(Material.AIR));
    }
  }
}
