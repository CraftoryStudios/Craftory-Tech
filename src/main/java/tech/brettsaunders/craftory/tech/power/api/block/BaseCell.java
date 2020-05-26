package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver, Externalizable {
  public static final int CAPACITY_BASE = 2000000;
  protected static final int amountReceive = 10;

  public BaseCell(Location location) {
    super(location);
    energyStorage = new EnergyStorage(CAPACITY_BASE);
    isReceiver = true;
    isProvider = true;
  }

  public BaseCell() {
    super();
    isReceiver = true;
    isProvider = true;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(energyStorage);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    energyStorage = (EnergyStorage) in.readObject();
  }

  @Override
  public void update() {
      transferEnergy();
  }

  @Override
  public int receiveEnergy(BlockFace from, int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, amountReceive), simulate);
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
}
