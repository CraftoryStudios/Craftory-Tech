package tech.brettsaunders.craftory.tech.power.core.block;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.tech.power.api.old.EnergyConfig;
import tech.brettsaunders.craftory.tech.power.api.old.GeneratorBase;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IMachineGUI;
import tech.brettsaunders.craftory.tech.power.core.manager.SolidFuelManager;

public class SolidFuelGenerator extends GeneratorBase implements IMachineGUI {

  protected static final EnergyConfig ENERGY_CONFIG = new EnergyConfig();
  protected static final int FUEL_SLOT = 22;
  public static final boolean enable = true; //Fix
  public static final int basePower = 40; //Fix

  private Inventory inventory;

  public SolidFuelGenerator() {
    super();
    inventory = Bukkit.createInventory(null, 54, "Solid Fuel Generator");
    inventory.setItem(13, new ItemStack(Material.STONE));
    inventory.setItem(21, new ItemStack(Material.STONE));
    inventory.setItem(23, new ItemStack(Material.STONE));
    inventory.setItem(31, new ItemStack(Material.STONE));
    //Inv
  }

  @Override
  protected EnergyConfig getEnergyConfig() {
    return ENERGY_CONFIG;
  }

  @Override
  protected boolean canStart() {
    return SolidFuelManager.getFuelEnergy(getFuelItem()) > 0;
  }

  @Override
  protected void processStart() {
    maxFuelRF = SolidFuelManager.getFuelEnergy(getFuelItem()) * energyMod / ENERGY_BASE;
    if (maxFuelRF != 0) {
      fuelRF += maxFuelRF;
      if (getFuelItem().getAmount() > 1) {
        getFuelItem().setAmount(getFuelItem().getAmount() - 1);
      } else {
        inventory.clear(FUEL_SLOT);
      }
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeUTF(getFuelItem().getType().name());
    out.writeInt(getFuelItem().getAmount());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    inventory.setItem(FUEL_SLOT, new ItemStack(Material.getMaterial(in.readUTF())));
    inventory.getItem(FUEL_SLOT).setAmount(in.readInt());
  }

  @Override
  public boolean updateOutputCache(BlockFace inputFrom) {
    return false;
  }

  protected ItemStack getFuelItem() {
    return inventory.getItem(FUEL_SLOT);
  }

  @Override
  public void showInterface(Player player) {

  }
}
