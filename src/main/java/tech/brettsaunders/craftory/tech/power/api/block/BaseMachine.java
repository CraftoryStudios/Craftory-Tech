package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver, Externalizable {

  protected static final int amountReceive = 10;

  public BaseMachine(Location location, byte level) {
    super(location, level);
    isReceiver = true;

  }

  public BaseMachine() {
    super();
    isReceiver = true;
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

  @Override
  public void update() {
    super.update();
  }

  public int howMuchDoYouNeed() { return 10;}
}
