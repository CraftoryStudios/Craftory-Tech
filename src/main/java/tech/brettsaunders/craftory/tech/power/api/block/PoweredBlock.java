package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import javax.sound.sampled.Line.Info;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;
import tech.brettsaunders.craftory.utils.HopperItemMovement;
import tech.brettsaunders.craftory.utils.Logger;

/**
 * A standard powered block Contains GUI, Tickable, EnergyInfo, Location and Energy Storage
 */
public abstract class PoweredBlock extends BlockGUI implements ITickable,
    IEnergyInfo, Listener, Externalizable {

  /* Static Constants Protected */
  protected static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  /* Static Constants Private */
  private static final long serialVersionUID = 10011L;
  /* Per Object Variables Saved */
  protected EnergyStorage energyStorage;
  protected Location location;
  protected int level;
  /* Hopper control variables */
  protected ItemStack[] inputSlots = {}; //The ItemStacks of the inputs
  protected ArrayList<Integer> inputLocations = new ArrayList<>();  //The inventory locations of inputs
  protected ItemStack[] outputSlots = {}; //The ItemStacks of the outputs
  protected ArrayList<Integer> outputLocations = new ArrayList<>(); //The inventory locations of outputs
  /* Per Object Variables Not-Saved */
  protected transient boolean isReceiver;
  protected transient boolean isProvider;
  /* Hopper stuff */
  protected transient int HOPPER_DELAY = 2;
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
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
  }

  /* Saving, Setup and Loading */
  public PoweredBlock() {
    super();
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(energyStorage);
    out.writeObject(location);
    out.writeInt(level);
    out.writeObject(inputSlots);
    out.writeObject(inputLocations);
    out.writeObject(outputSlots);
    out.writeObject(outputLocations);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    energyStorage = (EnergyStorage) in.readObject();
    location = (Location) in.readObject();
    level = in.readInt();
    try {
      inputSlots = (ItemStack[]) in.readObject();
      inputLocations = (ArrayList<Integer>) in.readObject();
      outputSlots = (ItemStack[]) in.readObject();
      outputLocations = (ArrayList<Integer>) in.readObject();
    } catch (Exception e) { }

  }

  /* Update Loop */
  @Override
  public void fastUpdate() {

  }

  private void processHoppers() {
    if(inventoryInterface==null) return;

    //Process incoming hoppers
    if(inputSlots.length > 0 && hopperInCounter != 0) hopperInCounter-=1;
    else {
      for (int i = 0; i < inputSlots.length; i++) {
        inputSlots[i] = inventoryInterface.getItem(inputLocations.get(i));
        ItemStack stack = HopperItemMovement.moveItemsIn(location,inputSlots[i]);
        if(stack !=null) {
          inputSlots[i] = stack;
          hopperInCounter = HOPPER_DELAY;
        }
        inventoryInterface.setItem(inputLocations.get(i), inputSlots[i]);
      }
    }
    if(outputSlots.length > 0 && hopperOutCounter != 0) hopperOutCounter-=1;
    else {
      for (int i = 0; i < outputSlots.length; i++) {
        outputSlots[i] = inventoryInterface.getItem(outputLocations.get(i));
        if(HopperItemMovement.moveItemsOut(location, outputSlots[i])){
          hopperOutCounter = HOPPER_DELAY;
        }
        inventoryInterface.setItem(outputLocations.get(i), outputSlots[i]);
      }
    }
    //Set inventory to equal slots
  }

  @Override
  public void slowUpdate() {
    updateInterface();
    processHoppers();
  }

  /* GUI Events */
  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != getInventory()) {
      return;
    }

    //Stop moving items from any slot but intractable ones
    if ((!inputLocations.contains(event.getRawSlot())) && (!outputLocations.contains(event.getRawSlot()))
        && event.getRawSlot() < 54) {
      event.setCancelled(true);
    }

    //Handle Shift Clicking Items
    if (event.isShiftClick() && event.getRawSlot() > 53) {
      event.setCancelled(true);
      ItemStack sourceItemStack = event.getCurrentItem();
      int amount = sourceItemStack.getAmount();
      for (Integer inputSlot : inputLocations) {
        ItemStack destinationItemStack = getInventory().getItem(inputSlot);
        if (destinationItemStack == null) {
          ItemStack itemStack1 = sourceItemStack.clone();
          itemStack1.setAmount(amount);
          getInventory().setItem(inputSlot, itemStack1);
          amount = 0;
          break;
        }
        if (destinationItemStack.getAmount() == destinationItemStack.getMaxStackSize()) continue;
        if (destinationItemStack.getType().equals(sourceItemStack.getType())) {
          int amountGive = Math.min(destinationItemStack.getMaxStackSize() - destinationItemStack.getAmount(), amount);
          destinationItemStack.setAmount(destinationItemStack.getAmount() + amountGive);
          getInventory().setItem(inputSlot, destinationItemStack);
          amount = amount - amountGive;
        }
      }
      sourceItemStack.setAmount(amount);
      event.getView().getBottomInventory().setItem(event.getSlot(), sourceItemStack);
    }

    if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ONE) {
      if (outputLocations.contains(event.getRawSlot())) {
        event.setCancelled(true);
      }
    }


  }

  /* Info Methods */
  public EnergyStorage getEnergyStorage() {
    return energyStorage;
  }

  protected boolean hasEnergy(int energy) {
    return energyStorage.getEnergyStored() >= energy;
  }

  public int getEnergySpace() {
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
