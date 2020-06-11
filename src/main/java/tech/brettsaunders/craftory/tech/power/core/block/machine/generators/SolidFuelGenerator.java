package tech.brettsaunders.craftory.tech.power.core.block.machine.generators;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.core.manager.SolidFuelManager;

public class SolidFuelGenerator extends BaseGenerator {

  /* Static Constants Private */
  private static final long serialVersionUID = 10020L;
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 80;

  protected static final int FUEL_SLOT = 12;
  private transient ItemStack fuelItem;

  /* Construction */
  public SolidFuelGenerator() {
    super();
  }

  /* Saving, Setup and Loading */
  public SolidFuelGenerator(Location location) {
    super(location, C_LEVEL, C_OUTPUT_AMOUNT);
    inputLocations.add(FUEL_SLOT);
  }


  @Override
  protected boolean canStart() {
    if (getFuelItem() == null) return false;
    return SolidFuelManager.getFuelEnergy(getFuelItem().getType().name()) > 0;
  }

  @Override
  protected void processStart() {
    maxFuelRE = SolidFuelManager.getFuelEnergy(getFuelItem().getType().name());
    fuelRE += maxFuelRE;
    consumeFuel();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    ItemStack fuelItem = getFuelItem();
    out.writeObject(fuelItem);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    fuelItem = (ItemStack) in.readObject();
  }

  protected ItemStack getFuelItem() {
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
    Inventory inventory = setInterfaceTitle("Fuel Generator", new FontImageWrapper("extra:cell"));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig));
    if (fuelItem != null) {
      getInventory().setItem(FUEL_SLOT, fuelItem);
    }
  }

}
