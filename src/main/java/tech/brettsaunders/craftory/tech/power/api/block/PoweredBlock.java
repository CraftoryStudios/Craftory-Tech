package tech.brettsaunders.craftory.tech.power.api.block;

import static tech.brettsaunders.craftory.CoreHolder.HOPPER_INTERACT_FACES;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

/**
 * A standard powered block Contains GUI, Tickable, EnergyInfo, Location and Energy Storage
 */
public abstract class PoweredBlock extends BlockGUI implements IEnergyInfo, Listener {

  /* Static Constants Protected */
  protected static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  /* Per Object Variables Saved */
  @Persistent
  protected EnergyStorage energyStorage;
  @Persistent
  protected int level;
  @Persistent
  protected HashMap<BlockFace, INTERACTABLEBLOCK> cachedSides;
  /* Hopper control variables */
  @Persistent
  protected ArrayList<ItemStack> inputSlots = new ArrayList<>(); //The ItemStacks of the inputs
  @Persistent
  protected ArrayList<Integer> inputLocations = new ArrayList<>();  //The inventory locations of inputs
  @Persistent
  protected ArrayList<ItemStack> outputSlots = new ArrayList<>(); //The ItemStacks of the outputs
  @Persistent
  protected ArrayList<Integer> outputLocations = new ArrayList<>(); //The inventory locations of outputs
  /* Per Object Variables Not-Saved */
  protected transient Inventory inventoryInterface;

  /* Construction */
  public PoweredBlock(Location location, String blockName, byte level) {
    super(location, blockName);
    cacheSides();
    this.energyStorage = new EnergyStorage(0);
    this.level = level;
    cachedSides = new HashMap<>();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
  }

  /* Saving, Setup and Loading */
  public PoweredBlock() {
    super();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
  }

  /* Update Loop */
  @Ticking(ticks = 8)
  public void processHoppers() {
    if (!(this instanceof IHopperInteract)) {
      return;
    }
    if (inventoryInterface == null) {
      return;
    }
    HashMap<BlockFace, Integer> inputFaces = ((IHopperInteract) this).getInputFaces();
    HashMap<BlockFace, Integer> outputFaces = ((IHopperInteract) this).getOutputFaces();
    inputFaces.forEach((face, slot) -> {
      if (cachedSides.containsKey(face) && cachedSides.get(face)
          .equals(INTERACTABLEBLOCK.HOPPER_IN)) {
        ItemStack stack = inventoryInterface.getItem(slot);
        ItemStack[] hopperItems = ((Hopper) location.getBlock().getRelative(face).getState())
            .getInventory().getContents();
        for (ItemStack item : hopperItems) {
          if (item == null) {
            continue;
          }
          if (stack == null) {
            stack = item.clone();
            stack.setAmount(1);
            item.setAmount(item.getAmount() - 1);
            break;
          } else if (stack.getType().toString().equals(item.getType().toString())
              && stack.getAmount() < stack.getMaxStackSize()) {
            stack.setAmount(stack.getAmount() + 1);
            item.setAmount(item.getAmount() - 1);
            break;
          }
        }
        inventoryInterface.setItem(slot, stack);
      }
    });

    outputFaces.forEach((face, slot) -> {
      if (cachedSides.containsKey(face) && cachedSides.get(face)
          .equals(INTERACTABLEBLOCK.HOPPER_OUT)) {
        ItemStack stack = inventoryInterface.getItem(slot);
        if (stack != null) {
          ItemStack toMove = stack.clone();
          toMove.setAmount(1);
          Inventory hopperInventory = ((Hopper) location.getBlock().getRelative(face).getState())
              .getInventory();
          HashMap<Integer, ItemStack> failedItems = hopperInventory.addItem(toMove);
          if (failedItems.isEmpty()) {
            stack.setAmount(stack.getAmount() - 1);
            inventoryInterface.setItem(slot, stack);
          }
        }
      }
    });
    //Set inventory to equal slots
  }

  /* GUI Events */
  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != getInventory()) {
      return;
    }

    //Stop moving items from any slot but intractable ones
    if ((!inputLocations.contains(event.getRawSlot())) && (!outputLocations
        .contains(event.getRawSlot()))
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
        if (destinationItemStack.getAmount() == destinationItemStack.getMaxStackSize()) {
          continue;
        }
        if (destinationItemStack.getType().equals(sourceItemStack.getType())) {
          int amountGive = Math
              .min(destinationItemStack.getMaxStackSize() - destinationItemStack.getAmount(),
                  amount);
          destinationItemStack.setAmount(destinationItemStack.getAmount() + amountGive);
          getInventory().setItem(inputSlot, destinationItemStack);
          amount = amount - amountGive;
        }
      }
      sourceItemStack.setAmount(amount);
      event.getView().getBottomInventory().setItem(event.getSlot(), sourceItemStack);
    }

    if (event.getAction() == InventoryAction.PLACE_ALL
        || event.getAction() == InventoryAction.PLACE_SOME
        || event.getAction() == InventoryAction.PLACE_ONE) {
      if (outputLocations.contains(event.getRawSlot())) {
        event.setCancelled(true);
      }
    }


  }

  public void setSideCache(BlockFace face, INTERACTABLEBLOCK type) {
    cachedSides.put(face, type);
  }

  /* Info Methods */
  public EnergyStorage getEnergyStorage() {
    return energyStorage;
  }

  protected boolean hasEnergy(int energy) {
    return energyStorage.getEnergyStored() >= energy;
  }

  private void cacheSides() {
    Block b;
    BlockFace facing;
    for (BlockFace face : HOPPER_INTERACT_FACES) {
      b = location.getBlock().getRelative(face);
      if (b.getType().equals(Material.HOPPER)) {
        facing = ((Directional) b.getBlockData()).getFacing();
        if (facing.equals(face.getOppositeFace())) {
          this.setSideCache(face, INTERACTABLEBLOCK.HOPPER_IN);
        }
        if (face.equals(BlockFace.DOWN)) {
          this.setSideCache(face, INTERACTABLEBLOCK.HOPPER_OUT);
        }
      } else if (b.getLocation() instanceof IEnergyReceiver) {
        this.setSideCache(face, INTERACTABLEBLOCK.RECIEVER);
      }
    }
  }

  public int getEnergySpace() {
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

  public int getInfoEnergyCapacity() {
    return energyStorage.getMaxEnergyStored();
  }
}
