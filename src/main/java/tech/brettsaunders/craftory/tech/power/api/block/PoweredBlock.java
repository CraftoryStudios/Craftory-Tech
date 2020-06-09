package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;
import tech.brettsaunders.craftory.utils.HopperItemMovement;

/**
 * A standard powered block Contains GUI, Tickable, EnergyInfo, Location and Energy Storage
 */
public abstract class PoweredBlock extends BlockGUI implements ITickable,
    IEnergyInfo, Externalizable {

  /* Static Constants Protected */
  protected static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  /* Static Constants Private */
  private static final long serialVersionUID = 10011L;
  /* Per Object Variables Saved */
  protected EnergyStorage energyStorage;
  protected Location location;
  protected byte level;
  protected ItemStack[] inputSlots = {};
  protected int[] inputLocations = {};
  protected ItemStack[] outputSlots = {};
  protected int[] outputLocations = {};
  /* Per Object Variables Not-Saved */
  protected transient boolean isReceiver;
  protected transient boolean isProvider;
  protected transient int HOPPER_DELAY = 8;
  protected transient int hopperInCounter = 0;
  protected transient int hopperOutCounter = 0;
  protected transient Inventory inventoryInterface;

  /* Construction */
  public PoweredBlock(Location location, byte level) {
    super();
    this.location = location;
    this.energyStorage = new EnergyStorage(0);
    isReceiver = false;
    isProvider = false;
    this.level = level;
    //Craftory.tickableBaseManager.addFastUpdate(this);
  }

  /* Saving, Setup and Loading */
  public PoweredBlock() {
    super();
    //Craftory.tickableBaseManager.addFastUpdate(this);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(energyStorage);
    out.writeObject(location);
    out.writeByte(level);
    out.writeObject(inputSlots);
    out.writeObject(inputLocations);
    out.writeObject(outputSlots);
    out.writeObject(outputLocations);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    energyStorage = (EnergyStorage) in.readObject();
    location = (Location) in.readObject();
    level = in.readByte();
    try {
      inputSlots = (ItemStack[]) in.readObject();
      inputLocations = (int[]) in.readObject();
      outputSlots = (ItemStack[]) in.readObject();
      outputLocations = (int[]) in.readObject();
    } catch (Exception e) { }

  }

  /* Update Loop */
  @Override
  public void update() {
    updateInterface();
    processHoppers();
  }

  private void processHoppers() {
    if(inventoryInterface==null) return;
    //Process incoming hoppers
    if(inputSlots.length > 0 && hopperInCounter != 0) hopperInCounter-=1;
    else {
      for (int i = 0; i < inputSlots.length; i++) {
        ItemStack stack = HopperItemMovement.moveItemsIn(location,inputSlots[i]);
        if(stack !=null) {
          inputSlots[i] = stack;
          hopperInCounter = HOPPER_DELAY;
        }
        getInventory().setItem(inputLocations[i], inputSlots[i]);
      }
    }
    if(outputSlots.length > 0 && hopperOutCounter != 0) hopperOutCounter-=1;
    else {
      boolean added = false;
      for (int i = 0; i < outputSlots.length; i++) {
        if(HopperItemMovement.moveItemsOut(location, outputSlots[i])){
          added = true;
        }
        getInventory().setItem(outputLocations[i], outputSlots[i]);
      }
      if(added) hopperOutCounter = HOPPER_DELAY;
    }
    //Set inventory to equal slots


  }

  /* Info Methods */
  public EnergyStorage getEnergyStorage() {
    return energyStorage;
  }

  protected boolean hasEnergy(int energy) {
    return energyStorage.getEnergyStored() >= energy;
  }

  protected int getEnergySpace() {
    return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
  }

  /* Block Type */
  public boolean isProvider() {
    return isProvider;
  }

  public boolean isReceiver() {
    return isReceiver;
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

  public int getInfoEnergyCapacity() {
    return energyStorage.getMaxEnergyStored();
  }
}
