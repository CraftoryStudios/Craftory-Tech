package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.util.HashSet;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.helpers.MathHelper;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IPowerInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public abstract class GeneratorBase implements ITickable, IEnergyProvider, IPowerInfo,
    Externalizable {

  protected static final int MIN_BASE_POWER = 10;
  protected static final int MAX_BASE_POWER = 1000;
  protected static int[] POWER_SCALING = { 100, 150, 200, 250, 300 };

  public static final int TIME_CONSTANT = 32;
  public static final byte LEVEL_MIN = 0;
  public static final byte LEVEL_MAX = 4;

  protected static final int ENERGY_BASE = 100;
  protected static final int POWER_BASE = 100;

  protected int fuelRF;
  protected int maxFuelRF;
  protected byte level;
  protected int lastEnergy;
  protected boolean isActive;
  protected boolean wasActive;

  protected EnergyStorage energyStorage;
  protected EnergyConfig energyConfig;

  public GeneratorBase() {
    isActive = true;
    energyConfig = getEnergyConfig().copy();
    energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 2);
  }

  public boolean setLevel(int level) {
      energyConfig.setDefaultParams(getBasePower(this.level));
      energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
      return true;
  }

  public final void setEnergyStored(int quantity) {
    energyStorage.setEnergyStored(quantity);
  }

  @Override
  public void update() {

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

  /* COMMON METHODS */
  protected abstract EnergyConfig getEnergyConfig();


  protected int getBasePower(int level) {
    return getEnergyConfig().maxPower * POWER_SCALING[MathHelper.clamp(level, LEVEL_MIN, LEVEL_MAX)] / POWER_BASE;
  }

  protected int calcEnergy() {

    if (energyStorage.getEnergyStored() <= energyConfig.minPowerLevel) {
      return energyConfig.maxPower;
    }
    if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
      return energyConfig.minPower;
    }
    return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / energyConfig.energyRamp;
  }

  protected int getScaledEnergyStored(int scale) {

    return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
  }

  protected abstract boolean canStart();

  protected boolean canFinish() {

    return fuelRF <= 0;
  }

  protected abstract void processStart();

  protected void processFinish() {

  }

  protected void processIdle() {

  }

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

  //TODO Implement Energy Transfer
  protected void transferEnergy() {
//    if (adjacentReceiver == null) {
//      if (adjacentHandler) {
//        energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[facing], Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
//      }
//      return;
//    }
//    energyStorage.modifyEnergyStored(-adjacentReceiver.receiveEnergy(EnumFacing.VALUES[facing ^ 1], Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
  }

  //TODO Might need method to reset variables on placement

  //TODO Save - energystorage, fuelrf

  //TODO GUI/ Invetory

  /* IEnergyProvider */
  @Override
  public int extractEnergy(BlockFace from, int maxExtract, boolean simulate) {
    return energyStorage.extractEnergy(Math.min(energyConfig.maxPower * 2, maxExtract), simulate);
  }

  @Override
  public int getEnergyStored(BlockFace from) {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(BlockFace from) {
    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(BlockFace from) {
    return true;
  }

  /* IEnergyInfo */
  @Override
  public int getInfoEnergyPerTick() {

    if (!isActive) {
      return 0;
    }
    return lastEnergy;
  }

  @Override
  public int getInfoMaxEnergyPerTick() {

    return energyConfig.maxPower;
  }

  @Override
  public int getInfoEnergyStored() {

    return energyStorage.getEnergyStored();
  }

}
