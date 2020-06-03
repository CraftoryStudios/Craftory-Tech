package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;

public abstract class BaseGenerator extends BaseProvider implements Externalizable {
  protected static final int CAPACITY_BASE = 40000;
  protected static final double[] CAPACITY_LEVEL = { 1, 1.5, 2, 3 };

  protected int fuelRF;
  protected int maxFuelRF;
  protected int lastEnergy;
  protected boolean isActive;
  protected boolean wasActive;

  public BaseGenerator() {
    super();
    isActive = true;
    isProvider = true;
    isActive = false;
    wasActive = false;
    lastEnergy = 0;
    maxFuelRF = 0;
  }

  public BaseGenerator(Location location, byte level, int outputAmount) {
    super(location, level, outputAmount);
    energyStorage = new EnergyStorage((int) (CAPACITY_BASE * CAPACITY_LEVEL[level]));
    addGUIComponent(new GBattery(getInventory(), energyStorage));
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
    energyStorage.modifyEnergyStored(80); //TODO need to fix look at old code
    fuelRF -= lastEnergy;
    lastEnergy = transferEnergy();
    return lastEnergy;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(fuelRF);
    out.writeObject(energyStorage);
    //TODO Missing
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fuelRF = in.readInt();
    energyStorage = (EnergyStorage) in.readObject();
    //TODO Missing
  }

  @Override
  public int getInfoEnergyPerTick() {
    if (!isActive) {
      return 0;
    }
    return lastEnergy;
  }

  @Override
  public int getInfoEnergyStored() {
    return energyStorage.getEnergyStored();
  }

}
