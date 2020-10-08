/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

public class BlockPlacer extends BaseMachine implements IHopperInteract {

  private static final byte C_LEVEL = 0;
  private static final int MAX_RECEIVE = 10000;
  private static final int SLOT = 22;
  protected static final Map<BlockFace, Integer> inputFaces = new EnumMap<>(BlockFace.class);
  protected static final Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  private static final int ENERGY_REQUIRED = 1000;
  private Location placeLoc;
  private int lastRedstoneStrength = 0;

  static {
    inputFaces.put(BlockFace.NORTH, SLOT);
    inputFaces.put(BlockFace.EAST, SLOT);
    inputFaces.put(BlockFace.SOUTH, SLOT);
    inputFaces.put(BlockFace.WEST, SLOT);
    inputFaces.put(BlockFace.UP, SLOT);

    outputFaces.put(BlockFace.DOWN, SLOT);
  }

  public BlockPlacer(Location location) {
    super(location, Blocks.BLOCK_PLACER, C_LEVEL, MAX_RECEIVE);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    setup();
    energyStorage = new EnergyStorage(40000);
  }

  public BlockPlacer() {
    super();
    setup();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeLoc = location.getBlock().getRelative(direction).getLocation();
  }

  private void setup() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0, SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void updateMachine() {
    //No Implementation
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.PLACER.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    this.inventoryInterface = inventory;
  }

  @EventHandler
  public void onRedstonePower(BlockPhysicsEvent e) {
    if (!e.getBlock().getLocation().equals(location)) {
      return;
    }
    if (placeLoc.getBlock().getType() != Material.AIR) {
      return;
    }
    if (lastRedstoneStrength != 0) {
      lastRedstoneStrength = e.getBlock().getBlockPower();
      return;
    } else if (e.getBlock().getBlockPower() > 0 && checkPowerRequirement()
        && inventoryInterface != null) {
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
    return energyStorage.getEnergyStored() > ENERGY_REQUIRED;
  }

  @Override
  protected void processComplete() {
    //No Implementation
  }

  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {
    //No Implementation
  }

  @Override
  public Map<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }

  @Override
  public Map<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }
}
