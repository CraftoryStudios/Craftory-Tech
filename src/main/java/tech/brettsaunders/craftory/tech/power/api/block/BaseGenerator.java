package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;

public abstract class BaseGenerator extends BaseProvider implements Externalizable {

  protected static final int ENERGY_BASE = 100;
  protected static final int POWER_BASE = 100;

  protected int minPower = 2;
  protected int maxPower = 20;
  protected int maxEnergy = 20000;
  protected int minPowerLevel = maxEnergy / 10;
  protected int maxPowerLevel = 9 * maxEnergy / 10;
  protected int energyRamp = maxPowerLevel / maxPower;

  protected int fuelRF;
  protected int maxFuelRF;
  protected byte level;
  protected int lastEnergy;
  protected boolean isActive;
  protected boolean wasActive;
  protected int energyMod = ENERGY_BASE;

  public BaseGenerator() {
    super();
    isActive = true;
    isProvider = true;
  }

  public BaseGenerator(Location location) {
    super(location);
    energyStorage = new EnergyStorage(maxEnergy, maxPower * 2);
    isActive = true;
    isProvider = true;
  }

  public final void setEnergyStored(int quantity) {
    energyStorage.setEnergyStored(quantity);
  }

  @Override
  public void update() {
    super.update();

    boolean curActive = isActive;

    if (isActive) {
      processTick();

      if (canFinish()) {
        if (!canStart()) {
          processOff();
        } else {
          processStart();
        }
      }
    } else {
      if (timeCheck()) {
        if (canStart()) {
          processStart();
          processTick();
          isActive = true;
        }
      }
    }
    if (timeCheck()) {
      if (!isActive) {
        processIdle();
      }
    }
  }

  //Time check factor to slow down check speed;
  protected boolean timeCheck() {
    //Core Props
    //return world.getTotalWorldTime() % TIME_CONSTANT == 0;
    return true;
  }

  protected int calcEnergy() {
    if (energyStorage.getEnergyStored() <= minPowerLevel) {
      return maxPower;
    }
    if (energyStorage.getEnergyStored() > maxPowerLevel) {
      return minPower;
    }
    return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / energyRamp;
  }

  protected abstract boolean canStart();

  protected boolean canFinish() {
    return fuelRF <= 0;
  }

  protected abstract void processStart();

  protected void processFinish(){}

  protected void processIdle(){}

  protected void processOff() {
    isActive = false;
    wasActive = true;
  }

  protected int processTick() {
    lastEnergy = calcEnergy();
    energyStorage.modifyEnergyStored(lastEnergy);
    fuelRF -= lastEnergy;
    transferEnergy();
    return lastEnergy;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(fuelRF);
    out.writeInt(maxFuelRF);
    out.writeByte(level);
    out.writeInt(lastEnergy);
    out.writeBoolean(isActive);
    out.writeBoolean(wasActive);
    out.writeInt(energyMod);
    out.writeObject(energyStorage);
    //TODO Missing
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fuelRF = in.readInt();
    maxFuelRF = in.readInt();
    level = in.readByte();
    lastEnergy = in.readInt();
    isActive = in.readBoolean();
    wasActive = in.readBoolean();
    energyMod = in.readInt();
    energyStorage = (EnergyStorage) in.readObject();
    //TODO Missing
  }

//  @Override
//  public int extractEnergy(BlockFace from, int maxExtract, boolean simulate) {
//    return energyStorage.extractEnergy(Math.min(maxPower * 2, maxExtract), simulate);
//  }

  @Override
  public int getInfoEnergyPerTick() {
    if (!isActive) {
      return 0;
    }
    return lastEnergy;
  }

  @Override
  public int getInfoMaxEnergyPerTick() {
    return maxPower;
  }

  @Override
  public int getInfoEnergyStored() {
    return energyStorage.getEnergyStored();
  }

}
