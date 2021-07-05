/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/
package tech.brettsaunders.craftory.tech.power.api.fluids;

import lombok.Getter;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;

/**
 * Implementation of {@link IEnergyStorage}
 */
public class FluidStorage {

  /* Static Constants Private */

  /* Static Constants Protected */

  /* Per Object Variables Saved */
  protected int fluid;
  protected int capacity;
  @Getter
  protected int maxReceive;
  protected int maxExtract;

  /* Per Object Variables Not-Saved */


  /* Construction */
  public FluidStorage(int capacity) {
    this(capacity, capacity, capacity);
  }

  public FluidStorage(int capacity, int maxTransfer) {
    this(capacity, maxTransfer, maxTransfer);
  }

  public FluidStorage(int capacity, int maxReceive, int maxExtract) {
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }

  public FluidStorage(int fluid, int capacity, int maxReceive, int maxExtract) {
    this.fluid = fluid;
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }


  /* Common Methods */
  public FluidStorage setCapacity(int capacity) {

    this.capacity = capacity;

    if (fluid > capacity) {
      fluid = capacity;
    }
    return this;
  }

  public FluidStorage setMaxTransfer(int maxTransfer) {
    setMaxReceive(maxTransfer);
    setMaxExtract(maxTransfer);
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

  public int modifyFluidStored(int fluid) {

    this.fluid += fluid;

    if (this.fluid > capacity) {
      this.fluid = capacity;
    } else if (this.fluid < 0) {
      this.fluid = 0;
    }
    return fluid;
  }

  public void forceAdd(int amount) {
    fluid += amount;
    if (fluid > capacity) {
      fluid = capacity;
    }
  }

  public int forceExtract(int amount) {
    if (amount > fluid) {
      amount = fluid;
      fluid = 0;
    } else {
      fluid -= amount;
    }
    return amount;
  }

  public int getSpace() {
    return capacity - fluid;
  }

  /* IEnergyStorage */
  public int receiveFluid(int maxReceive, boolean simulate) {

    int fluidReceived = Math.min(capacity - fluid, Math.min(this.maxReceive, maxReceive));

    if (!simulate) {
      fluid += fluidReceived;
    }
    return fluidReceived;
  }

  public int extractFluid(int maxExtract, boolean simulate) {

    int fluidExtracted = Math.min(fluid, Math.min(this.maxExtract, maxExtract));

    if (!simulate) {
      fluid -= fluidExtracted;
    }
    return fluidExtracted;
  }

  public int getFluidStored() {
    return fluid;
  }

  public void setFluidStored(int energy) {

    this.fluid = energy;

    if (this.fluid > capacity) {
      this.fluid = capacity;
    } else if (this.fluid < 0) {
      this.fluid = 0;
    }
  }

  public boolean isFull() {
    return fluid >= capacity;
  }

  public int getMaxFluidStored() {
    return capacity;
  }
}
