/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import static tech.brettsaunders.craftory.Constants.HOPPER_INTERACT_FACES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockUtils;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

/**
 * A standard powered block Contains GUI, Tickable, EnergyInfo, Location and Energy Storage
 */
public abstract class PoweredBlock extends BlockGUI implements IEnergyInfo, Listener {

  /* Static Constants Protected */
  private static final HashSet<InventoryAction> outputDisabledActions = new HashSet<>(Arrays
      .asList(InventoryAction.SWAP_WITH_CURSOR, InventoryAction.PLACE_ALL,
          InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME));
  /* Per Object Variables Saved */
  @Persistent
  protected EnergyStorage energyStorage;
  @Persistent
  protected int level;
  @Persistent
  protected Map<BlockFace, INTERACTABLEBLOCK> cachedSidesConfig;
  protected Map<BlockFace, CustomBlock> cachedSides;
  /* Hopper control variables */
  @Persistent
  protected ArrayList<ItemStack> inputSlots = new ArrayList<>(); //The ItemStacks of the inputs

  protected ArrayList<Integer> inputLocations = new ArrayList<>();  //The inventory locations of inputs
  @Persistent
  protected ArrayList<ItemStack> outputSlots = new ArrayList<>(); //The ItemStacks of the outputs

  protected ArrayList<Integer> outputLocations = new ArrayList<>(); //The inventory locations of outputs
  /* Per Object Variables Not-Saved */
  protected  Inventory inventoryInterface;
  private  boolean powered = false;

  /* Construction */
  protected PoweredBlock(Location location, String blockName, byte level) {
    super(location, blockName);
    cachedSidesConfig = new EnumMap<>(BlockFace.class);
    cachedSides = new EnumMap<>(BlockFace.class);
    this.energyStorage = new EnergyStorage(0);
    this.level = level;
    cacheSides();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
    powered = false;
  }

  /* Saving, Setup and Loading */
  protected PoweredBlock() {
    super();
    Events.registerEvents(this);
  }

  public void refreshSideCache() {
    if (cachedSides != null) {
      return;
    }
    cachedSides = new EnumMap<>(BlockFace.class);
    cachedSidesConfig.forEach(((blockFace, interactableblock) -> {
      if (interactableblock.equals(INTERACTABLEBLOCK.RECEIVER)) {
        cachedSides.put(blockFace, Craftory.customBlockManager
            .getCustomBlock(this.location.getBlock().getRelative(blockFace).getLocation()));
      }
    }));
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    powered = location.getBlock().isBlockPowered();
    if(inventoryInterface!=null) {
      //Load in items in machines
      for (int i = 0; i < inputLocations.size(); i++) {
        if (i >= inputSlots.size()) {
          break;
        }
        inventoryInterface.setItem(inputLocations.get(i), inputSlots.get(i));
      }

      for (int i = 0; i < outputLocations.size(); i++) {
        if (i >= outputSlots.size()) {
          break;
        }
        inventoryInterface.setItem(outputLocations.get(i), outputSlots.get(i));
      }
    }
  }

  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    inputSlots.clear();
    for (int i = 0; i < inputLocations.size(); i++) {
      inputSlots.add(i, inventoryInterface.getItem(inputLocations.get(i)));
    }

    outputSlots.clear();
    for (int i = 0; i < outputLocations.size(); i++) {
      outputSlots.add(i, inventoryInterface.getItem(outputLocations.get(i)));
    }

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
    Map<BlockFace, Integer> inputFaces = ((IHopperInteract) this).getInputFaces();
    Map<BlockFace, Integer> outputFaces = ((IHopperInteract) this).getOutputFaces();

    //Hopper Input
    inputFaces.forEach((face, slot) -> {
      if (cachedSidesConfig.containsKey(face) && cachedSidesConfig.get(face)
          .equals(INTERACTABLEBLOCK.HOPPER_IN)) {
        ItemStack stack = inventoryInterface.getItem(slot);
        final Block relative = location.getBlock().getRelative(face);
        if (!relative.isBlockPowered()) {
          ItemStack[] hopperItems = ((Hopper) relative.getState())
              .getInventory().getContents();
          for (ItemStack item : hopperItems) {
            if (item != null) {
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
          }
          inventoryInterface.setItem(slot, stack);
        }
      }
    });

    //Hopper Output
    outputFaces.forEach((face, slot) -> {
      if (cachedSidesConfig.containsKey(face) && cachedSidesConfig.get(face)
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
    if (event.getRawSlot() > 53) {
      //Handle Shift Clicking Items
      if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)
          || event.getCurrentItem().getAmount() == 0) {
        return;
      }
      if (event.isShiftClick()) {
        event.setCancelled(true);
        ItemStack sourceItemStack = event.getCurrentItem();
        if (sourceItemStack == null) {
          return;
        }
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
    } else {
      //Stop moving items from any slot but intractable ones
      if (!interactableSlots.contains(event.getRawSlot()) && outputDisabledActions.contains(event.getAction()) && outputLocations
          .contains(event.getRawSlot())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryInteract(final InventoryDragEvent event) {
    if (event.getInventory() != getInventory()) {
      return;
    }
    event.getRawSlots().forEach(slot -> {
      //Stop moving items from any slot but intractable ones
      if (slot <= 53 && !interactableSlots.contains(slot)) {
        event.setCancelled(true);
      }
    });
  }

  @EventHandler
  public void onPoweredStateUpdate(BlockRedstoneEvent e) {
    if (e.getBlock().getLocation() == location) {
      powered = e.getNewCurrent() > 0;
    }
  }

  public void setSideCache(BlockFace face, INTERACTABLEBLOCK type, CustomBlock customBlock) {
    cachedSidesConfig.put(face, type);
    if (type == INTERACTABLEBLOCK.NONE) {
      cachedSides.remove(face);
    } else {
      cachedSides.put(face, customBlock);
    }
  }

  public void setSideCache(BlockFace face, INTERACTABLEBLOCK type) {
    cachedSidesConfig.put(face, type);
  }

  protected boolean isBlockPowered() {
    return powered;
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
      } else if (PoweredBlockUtils.isEnergyReceiver(b.getLocation())) {
        this.setSideCache(face, INTERACTABLEBLOCK.RECEIVER,
            PoweredBlockUtils.getPoweredBlock(b.getLocation()));
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
