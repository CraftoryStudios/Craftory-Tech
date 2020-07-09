package tech.brettsaunders.craftory.tech.power.core.block.machine.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;

public class SolidFuelGenerator extends BaseGenerator {
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 80;

  /* Construction */
  public SolidFuelGenerator() {
    super();
    interactableSlots = new HashSet<>(Collections.singletonList(FUEL_SLOT));
  }

  /* Saving, Setup and Loading */
  public SolidFuelGenerator(Location location) {
    super(location, Blocks.SOLID_FUEL_GENERATOR, C_LEVEL, C_OUTPUT_AMOUNT);
    inputLocations.add(FUEL_SLOT);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    interactableSlots = new HashSet<>(Collections.singletonList(FUEL_SLOT));
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

}
