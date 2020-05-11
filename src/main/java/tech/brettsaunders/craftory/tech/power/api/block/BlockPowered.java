package tech.brettsaunders.craftory.tech.power.api.block;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.tech.power.api.interfaces.EnumFacing;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IPowerInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public abstract class BlockPowered implements IPowerInfo, IEnergyReceiver, ITickable {

  protected EnergyStorage energyStorage = new EnergyStorage(0);

  protected boolean hasEnergy(int energy) {

    return energyStorage.getEnergyStored() >= energy;
  }

  protected int getEnergySpace() {

    return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
  }

  /* GUI METHODS */
  public IEnergyStorage getEnergyStorage() {

    return energyStorage;
  }

  /* NBT METHODS */
  public void readFromNBT(NBTCompound nbt) {
    energyStorage.readFromNBT(nbt);
  }

  public NBTCompound writeToNBT(NBTCompound nbt) {
    energyStorage.writeToNBT(nbt);
    return nbt;
  }

  /* IPowerInfo */
  @Override
  public int getInfoEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoMaxEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoEnergyStored() {

    return energyStorage.getEnergyStored();
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

    return energyStorage.receiveEnergy(maxReceive, simulate);
  }

  @Override
  public int getEnergyStored(EnumFacing from) {

    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {

    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {

    return energyStorage.getMaxEnergyStored() > 0;
  }
}
