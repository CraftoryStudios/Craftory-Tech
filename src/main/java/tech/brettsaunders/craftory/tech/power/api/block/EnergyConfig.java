package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.DoubleToIntFunction;

public class EnergyConfig implements Externalizable {

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

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(minPower);
    out.writeInt(maxPower);
    out.writeInt(maxEnergy);
    out.writeInt(minPowerLevel);
    out.writeInt(maxPowerLevel);
    out.writeInt(energyRamp);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    minPower = in.readInt();
    maxPower = in.readInt();
    maxEnergy = in.readInt();
    minPowerLevel = in.readInt();
    maxPowerLevel = in.readInt();
    energyRamp = in.readInt();
  }
}
