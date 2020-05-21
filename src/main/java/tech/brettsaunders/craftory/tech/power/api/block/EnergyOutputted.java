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

public abstract class EnergyOutputted extends PoweredBlock implements IEnergyProvider,
    Externalizable {
  public static final Integer[] DEFAULT_SIDES_CONFIG = { 0, 0, 0, 0, 0, 0 };  //NORTH, EAST, SOUTH, WEST, UP, DOWN
  public static final int CONFIG_NONE = 0;
  public static final int CONFIG_OUTPUT = 1;
  public static final int CONFIG_INPUT = 2;
  protected ArrayList<Integer> sidesConfig = new ArrayList<>(6);
  protected ArrayList<Boolean> sidesCache = new ArrayList<>(6);

  public EnergyOutputted(Location location) {
    super(location);
    Collections.addAll(sidesConfig, DEFAULT_SIDES_CONFIG);
    generateSideCache();
  }

  public EnergyOutputted(){}

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
  public boolean updateOutputCache(BlockFace inputFrom) {
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
      sidesCache.set(side, true);
      return true;
    }
    return false;
  }

  public int insertEnergyIntoAdjacentEnergyReceiver(int side, int energy, boolean simulate) {
    Location targetLocation = this.location.getBlock().getRelative(faces[side]).getLocation();
    if (Craftory.getBlockPoweredManager().isPowerReciever(targetLocation)) {
      return Craftory.getBlockPoweredManager().getPoweredBlock(targetLocation).receiveEnergy(BlockFace.EAST, energy, simulate);
    } else {
      sidesCache.set(side, false);
    }
    return 0;
  }

  //TODO on block place add to cache
  private void generateSideCache() {
    int i = 0;
    for(BlockFace face : faces) {
      if (Craftory.getBlockPoweredManager().isPowerReciever(this.location.getBlock().getRelative(face).getLocation())) {
        sidesCache.add(i, true);
        Logger.info("Cached side " + i);
      } else {
        sidesCache.add(i, false);
      }
      i++;
    }
  }

}
