package tech.brettsaunders.craftory.tech.power.core.block.machine.generators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;

public class SolidFuelGenerator extends BaseGenerator {

  /* Static Constants Private */
  private static final long serialVersionUID = 10020L;
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 80;

  //protected static final int FUEL_SLOT = 22;
  //public static final boolean enable = true; //Fix
  //public static final int basePower = 40; //Fix

  /* Construction */
  public SolidFuelGenerator() {
    super();
  }

  /* Saving, Setup and Loading */
  public SolidFuelGenerator(Location location) {
    super(location, C_LEVEL, C_OUTPUT_AMOUNT);
  }


  @Override
  protected boolean canStart() {
    return 1 > 0;
//    return SolidFuelManager.getFuelEnergy(getFuelItem()) > 0;
  }

  @Override
  protected void processStart() {
//    maxFuelRF = SolidFuelManager.getFuelEnergy(getFuelItem()) * energyMod / ENERGY_BASE;
    maxFuelRF = outputAmount;
    if (maxFuelRF != 0) {
      fuelRF += maxFuelRF;
//      if (getFuelItem().getAmount() > 1) {
//        getFuelItem().setAmount(getFuelItem().getAmount() - 1);
//      } else {
//        inventory.clear(FUEL_SLOT);
//      }
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
//    out.writeUTF(getFuelItem().getType().name());
//    out.writeInt(getFuelItem().getAmount());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
//    inventory.setItem(FUEL_SLOT, new ItemStack(Material.getMaterial(in.readUTF())));
//    inventory.getItem(FUEL_SLOT).setAmount(in.readInt());
  }

//  protected ItemStack getFuelItem() {
//    return inventory.getItem(FUEL_SLOT);
//  }

//  @Override
//  public void showInterface(Player player) {
//
//  }
}
