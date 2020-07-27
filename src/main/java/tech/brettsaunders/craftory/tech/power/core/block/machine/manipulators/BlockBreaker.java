/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;

public class BlockBreaker extends BaseMachine {
  private static final byte C_LEVEL = 0;
  private static final int MAX_RECEIVE = 10000;
  private static final int SLOT = 22;

  private static final int ENERGY_REQUIRED = 1200;
  private Location breakLoc;
  private Location opposite;
  private int lastRedstoneStrength = 0;

  private Optional<Inventory> outputInventory;

  public BlockBreaker(Location location) {
    super(location, Blocks.BLOCK_BREAKER, C_LEVEL, MAX_RECEIVE);
    init();
    energyStorage = new EnergyStorage(40000);
    outputInventory = Optional.empty();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    breakLoc = location.getBlock().getRelative(direction).getLocation();
    opposite = location.getBlock().getRelative(direction.getOppositeFace()).getLocation();
    setOutputInventory(opposite.getBlock());
  }

  public BlockBreaker() {
    super();
    init();
    outputInventory = Optional.empty();
  }

  private void init() {
    outputLocations = new ArrayList<>();
    outputLocations.add(0,SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void updateMachine() {

  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.BLANK.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage, 13));
    this.inventoryInterface = inventory;
  }
  @EventHandler
  public void onChestPlace(BlockPlaceEvent e) {
    final Block blockPlaced = e.getBlockPlaced();
    if (!blockPlaced.getLocation().equals(opposite)) return;
    setOutputInventory(blockPlaced);
  }

  private void setOutputInventory(Block block) {
    if(block.getState() instanceof InventoryHolder) {
      InventoryHolder ih = (InventoryHolder) block.getState();
      outputInventory = Optional.of(ih.getInventory());
    }
  }

  @EventHandler
  public void onChestRemove(BlockBreakEvent e) {
    final Block block = e.getBlock();
    if (!block.getLocation().equals(opposite)) return;
    if (outputInventory.isPresent()) {
      outputInventory= Optional.empty();
    }
  }

  @EventHandler
  public void onRedstonePower(BlockPhysicsEvent e) {
    if (!e.getBlock().getLocation().equals(location)) return;
    if (lastRedstoneStrength != 0) {
      lastRedstoneStrength = e.getBlock().getBlockPower();
      return;
    } else if (e.getBlock().getBlockPower() > 0 && checkPowerRequirement()) {
      if (breakLoc.getBlock().isEmpty()) {
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED / 10);
      } else {
        Block block = breakLoc.getBlock();
        if (Craftory.customBlockManager.isCustomBlock(breakLoc)) {
          Optional<ItemStack> itemStack = Craftory.customBlockManager.breakCustomBlock(breakLoc);
          itemStack.ifPresent(itemStack1 -> dropItem(itemStack1));
        } else {
          block.getDrops().forEach(this::dropItem);
          block.setType(Material.AIR);
        }
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED);
      }
    }
    lastRedstoneStrength = e.getBlock().getBlockPower();
  }

  private void dropItem(ItemStack itemStack) {
    if (outputInventory.isPresent()) {
      HashMap<Integer,ItemStack> result = outputInventory.get().addItem(itemStack);
      if (result.size() > 0) {
        result.forEach((i,item) -> location.getWorld().dropItemNaturally(opposite,item));
      }
    } else {
      location.getWorld().dropItem(opposite, itemStack);
    }
  }

  private boolean checkPowerRequirement() {
    if(energyStorage.getEnergyStored() > ENERGY_REQUIRED) {
      return true;
    }
    return false;
  }

  @Override
  protected void processComplete() {

  }

  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {

  }
}
