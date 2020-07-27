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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

public class BlockPlacer extends BaseMachine implements IHopperInteract {
  private static final byte C_LEVEL = 0;
  private static final int MAX_RECEIVE = 10000;
  private static final int SLOT = 22;

  private static final int ENERGY_REQUIRED = 1000;
  private Location placeLoc;
  private int lastRedstoneStrength = 0;

  public BlockPlacer(Location location) {
    super(location, Blocks.BLOCK_PLACER, C_LEVEL, MAX_RECEIVE);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    init();
    energyStorage = new EnergyStorage(40000);
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeLoc = location.getBlock().getRelative(direction).getLocation();
  }

  public BlockPlacer() {
    super();
    init();
  }

  private void init() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0,SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void updateMachine() {

  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.PLACER.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    this.inventoryInterface = inventory;
  }

  @EventHandler
  public void onRedstonePower(BlockPhysicsEvent e) {
    if (!e.getBlock().getLocation().equals(location)) return;
    if (placeLoc.getBlock().getType() != Material.AIR) return;
    if (lastRedstoneStrength != 0) {
      lastRedstoneStrength = e.getBlock().getBlockPower();
      return;
    } else if (e.getBlock().getBlockPower() > 0 && checkPowerRequirement() && inventoryInterface != null) {
      final ItemStack item = inventoryInterface.getItem(SLOT);
      if (item == null) {
        lastRedstoneStrength = e.getBlock().getBlockPower();
        return;
      }
      if (item.getType() == Material.AIR || !item.getType().isBlock()) {
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED / 10);
      } else {
        if (CustomItemManager.isCustomItem(item, true)) {
          //No Custom Block Placing
        } else {
          placeLoc.getBlock().setType(item.getType());
        }
        item.setAmount(item.getAmount() - 1);
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED);
      }
    }
    lastRedstoneStrength = e.getBlock().getBlockPower();
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

  protected static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, SLOT);
      put(BlockFace.EAST, SLOT);
      put(BlockFace.SOUTH, SLOT);
      put(BlockFace.WEST, SLOT);
      put(BlockFace.UP, SLOT);
    }
  };

  protected static final HashMap<BlockFace, Integer> outputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.DOWN, SLOT);
    }
  };

  @Override
  public HashMap<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }

  @Override
  public HashMap<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }
}
