/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.CHARGE_KEY;
import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.MAX_CHARGE_KEY;
import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.isPoweredTool;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager;
import tech.brettsaunders.craftory.utils.Light;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver {

  protected static final int CAPACITY_BASE = 400000;
  protected static final int[] CAPACITY_LEVEL = {1, 5, 50, 200};
  protected static final int MAX_INPUT = 200;
  protected static final int[] INPUT_LEVEL = {1, 4, 40, 160};
  protected static final int CHARGE_SPEED_BASE = 5;
  protected static final int[] CHARGE_SPEED_LEVEL = {1,2,4,8};
  protected static final int ITEM_LOCATION = 50;
  /* Static Constants */

  protected  int currentLightLevel = -1;

  /* Construction */
  protected BaseCell(Location location, String blockName, byte level, int outputAmount) {
    super(location, blockName, level, outputAmount);
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    setup();
  }

  /* Saving, Setup and Loading */
  protected BaseCell() {
    super();
    setup();
  }

  private void setup() {
    inputLocations = new ArrayList<>();
    outputLocations = new ArrayList<>();
    inputLocations.add(ITEM_LOCATION);
    interactableSlots = new HashSet<>(Collections.singletonList(ITEM_LOCATION));
  }

  @Ticking(ticks = 100)
  public void updateLight() {
    if(!Craftory.plugin.isPluginLoaded("LightAPI")) {
      return;
    }
    int level = Math.round((getEnergyStored() / (float) getMaxEnergyStored()) * 10);
    if(currentLightLevel!=level) {
      if(currentLightLevel!=-1) {
        Light.deleteLight(location, false);
      }
      if(level>0) {
        Light.createLight(location, level, false);
      }
      currentLightLevel = level;
    }
  }

  @Ticking(ticks = 1200)
  public void  refreshLight() {
    if(!Craftory.plugin.isPluginLoaded("LightAPI")) {
      return;
    }
    if(currentLightLevel>0) {
      Light.deleteLight(location, false);
      Light.createLight(location, currentLightLevel, false);
    }
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
    inventoryInterface = createInterfaceInventory(displayName, Font.CELL_GUI.label + "");
    addGUIComponent(new GBattery(inventoryInterface, energyStorage));
    addGUIComponent(new GOutputConfig(inventoryInterface, sidesConfig, 23, true));

    if (inputSlots.isEmpty()) {
      inputSlots.add(0, new ItemStack(Material.AIR));
    }
  }
}
