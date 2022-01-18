/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;


import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOutputConfig;

public class BaseSolarGenerator extends BaseGenerator {

  static final boolean SOLAR_DURING_STORM = Utilities.config
      .getBoolean("generators.solarDuringStorms");
  private static final int BASE_CAPACITY = 50000;
  private static final int NOON_OUTPUT = 20;
  private static final int[] MULTIPLIERS = {1, 4, 16, 32};

  public BaseSolarGenerator(Location location, String blockName, byte level) {
    super(location, blockName, level, NOON_OUTPUT * MULTIPLIERS[level],
        BASE_CAPACITY * MULTIPLIERS[level]);
  }

  public BaseSolarGenerator() {
    super();
  }

  @Override
  protected boolean canStart() {
    if (!location.getWorld().getEnvironment().equals(Environment.NORMAL)) {
      return false;
    }
    if (SOLAR_DURING_STORM) {
      return location.getWorld().getTime() < 13000
          && location.clone().add(0, 1, 0).getBlock().getLightFromSky() == 15;
    }
    return !location.getWorld().isThundering() && location.getWorld().getTime() < 13000
        && location.clone().add(0, 1, 0).getBlock().getLightFromSky() == 15;

  }

  @Override
  protected boolean canFinish() {
    return !canStart();
  }

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced() * MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

  protected int calculateAmountProduced() {
    long time = location.getWorld().getTime();
    double diff = Math.abs(time - 6000) / 1000d; //might be too complex
    return ((int) Math.round((-0.555 * (diff * diff)))) + NOON_OUTPUT;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.SOLAR.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 12));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, 23, true));
    this.inventoryInterface = inventory;
  }
}
