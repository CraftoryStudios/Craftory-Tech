package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GTwoToOneMachine;
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelManager;

public class IronFoundry extends BaseFoundry {
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;
  protected static final int FUEL_SLOT = 48;

  @Persistent
  private ItemStack fuelItem;

  @Persistent
  private int fuel = 0;

  /* Construction */
  public IronFoundry(Location location) {
    super(location, Blocks.IRON_ELECTRIC_FOUNDRY, C_LEVEL);
    interactableSlots.add(FUEL_SLOT);
    inputLocations.add(FUEL_SLOT);
  }

  /* Saving, Setup and Loading */
  public IronFoundry() {
    super();
    interactableSlots.add(FUEL_SLOT);
    inputLocations.add(FUEL_SLOT);
  }

  @Override
  protected boolean hasSufficientEnergy() {
    if(fuel > 0) {
      fuel -=1;
      return true;
    } else if(fuelItem!=null) {
      int value = (SolidFuelManager.getFuelEnergy(fuelItem.getType().name())/15);
      if(value > 0){
        fuel += value-1;
        fuelItem.setAmount(fuelItem.getAmount()-1);
        getInventory().setItem(FUEL_SLOT,fuelItem);
        return true;
      }
    }
    return false;
  }

  @Override
  protected void updateSlots(){
    super.updateSlots();
    fuelItem = getInventory().getItem(FUEL_SLOT);
  }


  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Foundry", Font.IRON_FOUNDRY_GUI.label + "");
    addGUIComponent(
        new GTwoToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION1, INPUT_LOCATION2,
            OUTPUT_LOCATION));
    addGUIComponent(new GIndicator(inventory, runningContainer, 21));
    if (inputSlots.size() == 0) inputSlots.add(0, new ItemStack(Material.AIR));
    if (inputSlots.size() == 0) inputSlots.add(1, new ItemStack(Material.AIR));
    if (outputSlots.size() == 0) outputSlots.add(0, new ItemStack(Material.AIR));
    inventory.setItem(INPUT_LOCATION1, inputSlots.get(0));
    inventory.setItem(INPUT_LOCATION2, inputSlots.get(1));
    inventory.setItem(OUTPUT_LOCATION, outputSlots.get(0));
    this.inventoryInterface = inventory;
  }


}
