package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;

public abstract class BaseProvider extends PoweredBlock implements IEnergyProvider,
    Externalizable {

  /* Static Constants Protected */
  protected static final Boolean[] DEFAULT_SIDES_CONFIG = {false, false, false, false, false,
      false};  //NORTH, EAST, SOUTH, WEST, UP, DOWN
  /* Static Constants Private */
  private static final long serialVersionUID = 10008L;
  /* Per Object Variables Saved */
  protected int maxOutput;
  protected ArrayList<Boolean> sidesConfig;

  /* Per Object Variables Not-Saved */


  /* Construction */
  public BaseProvider(Location location, byte level, int maxOutput) {
    super(location, level);
    this.maxOutput = maxOutput;
    init();
    Collections.addAll(sidesConfig, DEFAULT_SIDES_CONFIG);
  }

  /* Saving, Setup and Loading */
  public BaseProvider() {
    super();
    init();
  }

  /* Common Load and Construction */
  private void init() {
    sidesConfig = new ArrayList<>(6);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(sidesConfig);
    out.writeInt(maxOutput);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    sidesConfig = (ArrayList<Boolean>) in.readObject();
    maxOutput = in.readInt();
  }

  /* Update Loop */
  protected void transferEnergy() {
    int amountTransferred = 0;
    for (int i = 0; i < sidesConfig.size(); i++) {
      if (sidesConfig.get(i)) {
        /*if (sidesCache.get(i)) {
          amountTransferred += energyStorage
              .modifyEnergyStored(-insertEnergyIntoAdjacentEnergyReceiver(i,
                  Math.min(maxOutput, energyStorage.getEnergyStored()), false));
        }*/
        if (cachedSides.containsKey(faces[i]) && cachedSides.get(faces[i])
            .equals(INTERACTABLEBLOCK.RECIEVER)) {
          amountTransferred += energyStorage
              .modifyEnergyStored(-insertEnergyIntoAdjacentEnergyReceiver(i,
                  Math.min(maxOutput, energyStorage.getEnergyStored()), false));
        }
      }
    }
  }

  @Ticking(ticks = 1)
  public void updateProvider() {
    transferEnergy();
  }

  //TODO compare to energyStorage.extractEnergy
  public int retrieveEnergy(int energy) {
    int energyExtracted = Math.min(getEnergyStored(), Math.min(energy, maxOutput));
    energyStorage.modifyEnergyStored(-energyExtracted);
    return energyExtracted;
  }


  /* Internal Helper Functions */


  public int insertEnergyIntoAdjacentEnergyReceiver(int side, int energy, boolean simulate) {
    Location targetLocation = this.location.getBlock().getRelative(faces[side]).getLocation();
    if (Craftory.getBlockPoweredManager().isReceiver(targetLocation)) {
      if (Craftory.getBlockPoweredManager().isProvider(targetLocation)) {
        return ((BaseCell) Craftory.getBlockPoweredManager().getPoweredBlock(targetLocation))
            .receiveEnergy(energy, simulate);
      } else {
        return ((BaseMachine) Craftory.getBlockPoweredManager().getPoweredBlock(targetLocation))
            .receiveEnergy(energy, simulate);
      }
    } else {
      //sidesCache.set(side, false);
    }
    return 0;
  }


  public ArrayList<Boolean> getSideConfig() {
    return sidesConfig;
  }

  public void setSidesConfig(ArrayList<Boolean> config) {
    sidesConfig.clear();
    sidesConfig.addAll(config);
  }

  /* IEnergyHandler */
  @Override
  public int getEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return energyStorage.getMaxEnergyStored();
  }

  /* IEnergyInfo */
  @Override
  public int getInfoMaxEnergyPerTick() {
    return maxOutput;
  }

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int getMaxOutput() {
    return maxOutput;
  }

  public int getEnergyAvailable() {
    return Math.min(energyStorage.energy, maxOutput);
  }
}
