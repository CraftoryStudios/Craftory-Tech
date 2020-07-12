package tech.brettsaunders.craftory.tech.power.api.fluids;

import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

public class FluidStorage extends EnergyStorage {
  /* Per Object Variables Saved */


  /* Per Object Variables Not-Saved */


  /* Construction */
  public FluidStorage(int capacity) { this(capacity, capacity, capacity); }

  public FluidStorage(int capacity, int maxTransfer) {
    this(capacity, maxTransfer, maxTransfer);
  }

  public FluidStorage(int capacity, int maxReceive, int maxExtract) {
    super(capacity,maxReceive,maxExtract);
  }

  public FluidStorage(int fluid, int capacity, int maxReceive, int maxExtract) {
    super(fluid,capacity,maxReceive,maxExtract);
  }


  /* Common Methods */
  public FluidStorage setCapacity(int capacity) {
    super.setCapacity(capacity);
    return this;
  }

  public FluidStorage setMaxTransfer(int maxTransfer) {
    super.setMaxTransfer(maxTransfer);
    return this;
  }

  public int getMaxReceive() {
    return maxReceive;
  }

  public void setMaxReceive(int maxReceive) {
    this.maxReceive = maxReceive;
  }

  public int getMaxExtract() {
    return maxExtract;
  }

  public void setMaxExtract(int maxExtract) {
    this.maxExtract = maxExtract;
  }

  public int modifyFluidStored(int amount) {
    return super.modifyEnergyStored(amount);
  }


  public int receiveFluid(int maxReceive, boolean simulate) {
    return super.receiveEnergy(maxReceive, simulate);
  }

  public int extractFluid(int maxExtract, boolean simulate) {
    return super.extractEnergy(maxExtract, simulate);
  }

  public int getFluidStored() {
    return energy;
  }

  public void setFluidStored(int amount) {
    super.setEnergyStored(amount);
  }

  public int getMaxFluidStored() {
    return capacity;
  }
}
