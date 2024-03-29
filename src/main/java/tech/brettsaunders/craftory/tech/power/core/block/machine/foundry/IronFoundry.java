/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GTwoToOneMachine;
import tech.brettsaunders.craftory.tech.power.core.block.generators.SolidFuelManager;

public class IronFoundry extends BaseFoundry {

  protected static final int FUEL_SLOT = 48;
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;
  @Persistent
  private ItemStack fuelItem;

  @Persistent
  private int fuel = 0;

  /* Construction */
  public IronFoundry(Location location, Player p) {
    super(location, Blocks.IRON_FOUNDRY, C_LEVEL);
    interactableSlots.add(FUEL_SLOT);
    inputLocations.add(FUEL_SLOT);
    energyStorage.setCapacity(0);
  }

  /* Saving, Setup and Loading */
  public IronFoundry() {
    super();
    interactableSlots.add(FUEL_SLOT);
    inputLocations.add(FUEL_SLOT);
    energyStorage = new EnergyStorage(0);
  }

  @Override
  protected boolean hasSufficientEnergy() {
    if (fuel > 0) {
      fuel -= 1;
      return true;
    } else if (fuelItem != null) {
      int value = (SolidFuelManager.getFuelEnergy(fuelItem.getType().name()) / 15);
      if (value > 0) {
        fuel += value - 1;
        fuelItem.setAmount(fuelItem.getAmount() - 1);
        getInventory().setItem(FUEL_SLOT, fuelItem);
        return true;
      }
    }
    return false;
  }

  @Override
  protected void updateSlots() {
    super.updateSlots();
    fuelItem = getInventory().getItem(FUEL_SLOT);
  }

  @Override
  protected void playSound() {
    location.getWorld().playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1, 1);
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.IRON_FOUNDRY_GUI.label + "");
    addGUIComponent(
        new GTwoToOneMachine(inventory, 23, progressContainer
        ));
    addGUIComponent(new GIndicator(inventory, runningContainer, 21));
    if (inputSlots.isEmpty() || inputSlots.get(0) == null) {
      inputSlots.add(0, new ItemStack(Material.AIR));
    }
    if (inputSlots.size() < 2 || inputSlots.get(1) == null) {
      inputSlots.add(1, new ItemStack(Material.AIR));
    }
    if (outputSlots.isEmpty() || outputSlots.get(0) == null) {
      outputSlots.add(0, new ItemStack(Material.AIR));
    }
    inventory.setItem(INPUT_LOCATION1, inputSlots.get(0));
    inventory.setItem(INPUT_LOCATION2, inputSlots.get(1));
    inventory.setItem(OUTPUT_LOCATION, outputSlots.get(0));
    this.inventoryInterface = inventory;
  }


}
