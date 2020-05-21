package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

/**
 * A standard powered block
 * Contains GUI, Tickable, EnergyInfo, Location and Energy Storage
 */
public abstract class PoweredBlock extends BlockGUI implements ITickable,
    IEnergyInfo, Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 100000001L;
  public static final BlockFace faces[] = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

  /* Per Object Variables */
  protected EnergyStorage energyStorage;
  protected Location location;

  /* Construction */
  public PoweredBlock(Location location) {
    this.location = location;
    this.energyStorage = new EnergyStorage(0);
    init();
    //TODO Register new Block with Manager
    //Craftory.getBlockPoweredManager().addPoweredBlock(location, this);
  }

  /* Shared Startup and Loading */
  public void init() {
    //Register for Base Updates
    Craftory.tickableBaseManager.addBaseTickable(this);
    //TODO Reigster for place and break events
  }

  /* Saving, Setup and Loading */
  public PoweredBlock(){
    init();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(energyStorage);
    out.writeObject(location);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    energyStorage = (EnergyStorage) in.readObject();
    location = (Location) in.readObject();
  }

  /* Info Methods */
  protected IEnergyStorage getEnergyStorage(){
    return energyStorage;
  }

  protected boolean hasEnergy(int energy) {

    return energyStorage.getEnergyStored() >= energy;
  }

  protected int getEnergySpace() {

    return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
  }

  /* IEnergyInfo */
  @Override
  public int getInfoEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoMaxEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoEnergyStored() {

    return energyStorage.getEnergyStored();
  }
}
