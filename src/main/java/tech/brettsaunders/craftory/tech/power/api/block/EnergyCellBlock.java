package tech.brettsaunders.craftory.tech.power.api.block;

import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public class EnergyCellBlock extends EnergyOutputted implements IEnergyReceiver, IEnergyInfo {

  @Override
  public int receiveEnergy(BlockFace from, int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public void updateNeighbourProviders() {

  }

  @Override
  public int getEnergyStored(BlockFace from) {
    return 0;
  }

  @Override
  public int getMaxEnergyStored(BlockFace from) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(BlockFace from) {
    return false;
  }

  @Override
  public void update() {

  }

  @Override
  public int extractEnergy(BlockFace from, int maxExtract, boolean simulate) {
    return 0;
  }
}
