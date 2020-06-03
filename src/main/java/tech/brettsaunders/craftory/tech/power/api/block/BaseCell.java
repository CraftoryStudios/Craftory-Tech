package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver, Externalizable {
  protected static final int CAPACITY_BASE = 400000;
  protected static final int[] CAPACITY_LEVEL = { 1, 5, 50, 200 };
  protected static final int MAX_INPUT = 200;
  protected static final int[] INPUT_LEVEL = { 1, 4, 40, 160 };

  public BaseCell(Location location) {
    super(location);
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
    isReceiver = true;
    isProvider = true;
    addGUIComponent(new GBattery(getInventory(), energyStorage));
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
    super.update();
    transferEnergy();
  }

  @Override
  public int receiveEnergy(BlockFace from, int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, MAX_INPUT * INPUT_LEVEL[level]), simulate);
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
