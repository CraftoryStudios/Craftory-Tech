package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.utils.Logger;

public abstract class BaseProvider extends PoweredBlock implements IEnergyProvider,
    Externalizable {
  public static final Integer[] DEFAULT_SIDES_CONFIG = { 1, 1, 1, 1, 1, 1 };  //NORTH, EAST, SOUTH, WEST, UP, DOWN
  public static final int CONFIG_NONE = 0;
  public static final int CONFIG_OUTPUT = 1;
  public static final int CONFIG_INPUT = 2;
  protected static final int amountToSend = 10;

  protected ArrayList<Integer> sidesConfig = new ArrayList<>(6);
  protected ArrayList<Boolean> sidesCache = new ArrayList<>(6);

  public BaseProvider(Location location) {
    super(location);
    Collections.addAll(sidesConfig, DEFAULT_SIDES_CONFIG);
    generateSideCache();
  }

  public BaseProvider(){}

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(sidesConfig);
    out.writeObject(sidesCache);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    sidesConfig = (ArrayList<Integer>) in.readObject();
    sidesCache = (ArrayList<Boolean>) in.readObject();
  }

  @Override
  public boolean updateOutputCache(BlockFace inputFrom, Boolean setTo) {
    //NORTH, EAST, SOUTH, WEST, UP, DOWN
    int side = -1;
    switch (inputFrom) {
      case NORTH: side = 0;
        break;
      case EAST: side = 1;
        break;
      case SOUTH: side = 2;
        break;
      case WEST: side = 3;
        break;
      case UP: side = 4;
        break;
      case DOWN: side = 5;
        break;
    }
    if (side != -1) {
      sidesCache.set(side, setTo);
      return true;
    }
    return false;
  }

  public int insertEnergyIntoAdjacentEnergyReceiver(int side, int energy, boolean simulate) {
    Location targetLocation = this.location.getBlock().getRelative(faces[side]).getLocation();
    if (Craftory.getBlockPoweredManager().isReceiver(targetLocation)) {
      if (Craftory.getBlockPoweredManager().isProvider(targetLocation)) {
        return ((BaseCell) Craftory.getBlockPoweredManager().getPoweredBlock(targetLocation))
            .receiveEnergy(BlockFace.EAST, energy, simulate);
      } else {
        return ((BaseMachine) Craftory.getBlockPoweredManager().getPoweredBlock(targetLocation))
            .receiveEnergy(BlockFace.EAST, energy, simulate);
      }
    } else {
      sidesCache.set(side, false);
    }
    return 0;
  }

  protected void transferEnergy() {
    for (int i = 0; i < sidesConfig.size(); i++) {
      if (sidesConfig.get(i) == CONFIG_OUTPUT) {
        if (sidesCache.get(i)) {
          energyStorage.modifyEnergyStored(-insertEnergyIntoAdjacentEnergyReceiver(i,
              Math.min(amountToSend, energyStorage.getEnergyStored()), false));
        }
      }
    }
  }

  private void generateSideCache() {
    int i = 0;
    for(BlockFace face : faces) {
      if (Craftory.getBlockPoweredManager().isReceiver(this.location.getBlock().getRelative(face).getLocation())) {
        sidesCache.add(i, true);
        Logger.info("Cached side " + i);
      } else {
        sidesCache.add(i, false);
      }
      i++;
    }
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
