package tech.brettsaunders.craftory.tech.power.api.block;

public class EnergyConfig {

  public int minPower = 2;
  public int maxPower = 20;
  public int maxEnergy = 20000;
  public int minPowerLevel = maxEnergy / 10;
  public int maxPowerLevel = 9 * maxEnergy / 10;
  public int energyRamp = maxPowerLevel / maxPower;

  public EnergyConfig() {

  }

  public EnergyConfig(EnergyConfig config) {

    this.minPower = config.minPower;
    this.maxPower = config.maxPower;
    this.maxEnergy = config.maxEnergy;
    this.minPowerLevel = config.minPowerLevel;
    this.maxPowerLevel = config.maxPowerLevel;
    this.energyRamp = config.energyRamp;
  }

  public EnergyConfig copy() {

    return new EnergyConfig(this);
  }

  public boolean setDefaultParams(int basePower) {

    maxPower = basePower;
    minPower = basePower / 10;
    maxEnergy = basePower * 1000;
    maxPowerLevel = 9 * maxEnergy / 10;
    minPowerLevel = maxEnergy / 10;
    energyRamp = maxPowerLevel / basePower;

    return true;
  }
}
