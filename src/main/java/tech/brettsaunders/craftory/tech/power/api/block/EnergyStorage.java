/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;

/**
 * Implementation of {@link IEnergyStorage}
 */
public class EnergyStorage implements IEnergyStorage {

  /* Static Constants Private */

  /* Static Constants Protected */

  /* Per Object Variables Saved */
  protected int energy;
  protected int capacity;
  protected int maxReceive;
  protected int maxExtract;

  /* Per Object Variables Not-Saved */


  /* Construction */
  public EnergyStorage(int capacity) {
    this(capacity, capacity, capacity);
  }

  public EnergyStorage(int capacity, int maxTransfer) {
    this(capacity, maxTransfer, maxTransfer);
  }

  public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }

  public EnergyStorage(int energy, int capacity, int maxReceive, int maxExtract) {
    this.energy = energy;
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }


  /* Common Methods */
  public EnergyStorage setCapacity(int capacity) {

    this.capacity = capacity;

    if (energy > capacity) {
      energy = capacity;
    }
    return this;
  }

  public EnergyStorage setMaxTransfer(int maxTransfer) {

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

  public int modifyEnergyStored(int energy) {

    this.energy += energy;

    if (this.energy > capacity) {
      this.energy = capacity;
    } else if (this.energy < 0) {
      this.energy = 0;
    }
    return energy;
  }

  /* IEnergyStorage */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {

    int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

    if (!simulate) {
      energy += energyReceived;
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {

    int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

    if (!simulate) {
      energy -= energyExtracted;
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored() {
    return energy;
  }

  public void setEnergyStored(int energy) {

    this.energy = energy;

    if (this.energy > capacity) {
      this.energy = capacity;
    } else if (this.energy < 0) {
      this.energy = 0;
    }
  }

  public boolean isFull() {
    return energy>=capacity;
  }

  @Override
  public int getMaxEnergyStored() {
    return capacity;
  }
}
