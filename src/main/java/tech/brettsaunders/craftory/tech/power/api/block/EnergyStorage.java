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

import java.util.Objects;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
  @Getter
  protected BossBar energyBar;

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
    return energy >= capacity;
  }

  @Override
  public int getMaxEnergyStored() {
    return capacity;
  }

  public String getEnergyBarTitle() {
    return "Energy "+getEnergyStored() + "/" + getMaxEnergyStored();
  }

  public double getEnergyBarProgress() {
    return (double) getEnergyStored() / (double) getMaxEnergyStored();
  }

  public BarColor getEnergyBarColour() {
    switch ((int) (getEnergyBarProgress() * 2)) {
      case 0:
        return BarColor.RED;
      case 1:
        return BarColor.YELLOW;
      case 2:
        return BarColor.GREEN;
    }
    return BarColor.WHITE;
  }

  public BarStyle getEnergyBarStyle() {
    return BarStyle.SEGMENTED_20;
  }

  public BarFlag[] getEnergyBarFlags() {
    return new BarFlag[0];
  }

  public void setupEnergyBar() {
    energyBar = Bukkit.createBossBar(getEnergyBarTitle(), getEnergyBarColour(), getEnergyBarStyle()
        , getEnergyBarFlags());
  }

  public void updateEnergyBar() {
    if (Objects.isNull(energyBar)) {
      setupEnergyBar();
    } else {
      // Title
      if (!energyBar.getTitle().equals(getEnergyBarTitle())) {
        energyBar.setTitle(getEnergyBarTitle());
      }
      // Progress
      if (energyBar.getProgress() != getEnergyBarProgress()) {
        energyBar.setProgress(getEnergyBarProgress());
      }
      // Color
      if (!energyBar.getColor().equals(getEnergyBarColour())) {
        energyBar.setColor(getEnergyBarColour());
      }
      // Style
      if (!energyBar.getStyle().equals(getEnergyBarStyle())) {
        energyBar.setStyle(getEnergyBarStyle());
      }
    }
  }
}
