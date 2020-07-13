package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.CoreHolder.FLUIDS;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.fluids.FluidStorage;

public class GTank extends G21PointBar{

  private final FluidStorage storage;
  private FLUIDS fluid = FLUIDS.LAVA;

  public GTank(Inventory inventory, FluidStorage storage, int top_slot) {
    super(inventory, top_slot);
    this.storage = storage;
  }

  public GTank(Inventory inventory, FluidStorage storage) {
    this(inventory, storage, 12);
  }

  @Override
  String getDisplayName() {
    return ChatColor.RESET + Utilities.langProperties.getProperty(fluid.toString()) + Utilities.langProperties.getProperty("Stored")+": " + Utilities.rawFluidToPrefixed(storage.getFluidStored());
  }

  @Override
  double getAmountFilled() {
    return ((double) storage.getFluidStored() / (double) storage.getMaxFluidStored()) * (double) 100;
  }

  @Override
  String getItemName() {

    return fluid.toString();
  }
}
