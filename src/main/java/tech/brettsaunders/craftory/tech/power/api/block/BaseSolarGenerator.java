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
import org.bukkit.World.Environment;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;

public class BaseSolarGenerator extends BaseGenerator{

  private static final int BASE_CAPACITY = 50000;
  private static final int NOON_OUTPUT = 20;

  private static final int[] MULTIPLIERS = {1,4,16,32};
  static final boolean solarDuringStorm = Utilities.config.getBoolean("generators.solarDuringStorms");
  public BaseSolarGenerator(Location location, String blockName, byte level) {
    super(location,blockName,level, NOON_OUTPUT *MULTIPLIERS[level],BASE_CAPACITY*MULTIPLIERS[level]);
  }
  public BaseSolarGenerator() {super();}

  @Override
  protected boolean canStart() {
    if(!location.getWorld().getEnvironment().equals(Environment.NORMAL)) return false;
    if(solarDuringStorm) return location.getWorld().getTime() < 13000 && location.clone().add(0,1,1).getBlock().getLightFromSky()==15;
    return !location.getWorld().isThundering() && location.getWorld().getTime() < 13000 && location.clone().add(0,1,1).getBlock().getLightFromSky()==15;

  }

  @Override
  protected boolean canFinish() {
    return !canStart();
  }

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced() *MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

  protected int calculateAmountProduced() {
    long time = location.getWorld().getTime();
    double diff = Math.abs(time-6000)/1000d; //might be too complex
    int amount =  ((int) Math.round((-0.555*(diff*diff))))+ NOON_OUTPUT; //same for this
    return amount;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.BLANK.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 31));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, true));
    this.inventoryInterface = inventory;
  }

}
