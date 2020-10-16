/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.tech.power.core.block.storage_drive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Constants.Items;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.storage_drive.StorageDrive;

public class DriveViewer extends BaseMachine {

  private static final int DRIVE_SLOT = 15;
  private static final int[] CONTENT_SLOTS = {16,24,25,33,34,42,43};
  private static final Set<InventoryAction> PLACE_ACTIONS = new HashSet<>(Arrays
      .asList(InventoryAction.PLACE_ALL,
          InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_SWAP));
  private static final Set<InventoryAction> PICKUP_ACTIONS = new HashSet<>(Arrays
      .asList(InventoryAction.PICKUP_ALL,
          InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.SWAP_WITH_CURSOR,
          InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.HOTBAR_MOVE_AND_READD));
  @Persistent
  private ItemStack drive;

  private boolean running = false;

  public DriveViewer(Location location) {
    super(location, Blocks.DRIVE_VIEWER, (byte) 0, 200);
    setup();
    energyStorage = new EnergyStorage(1000);
  }

  public DriveViewer() {
    super();
    setup();
  }

  private void setup() {
    outputLocations = new ArrayList<>();
    inputLocations = new ArrayList<>();
    inputLocations.add(DRIVE_SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(DRIVE_SLOT));
    this.energyConsumption = 10;
  }

  /* Update Loop */
  @Ticking(ticks = 1)
  @Override
  public void updateMachine() {
    drive = inventoryInterface.getItem(DRIVE_SLOT);
    inputSlots.set(0, drive);
    if(drive==null || drive.getType().equals(Material.AIR)) return;
    if(inventoryInterface.getViewers().isEmpty() || !CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_DRIVE)) return;
    if(hasSufficientEnergy()) {
      if(!running) {
        loadItems();
        running = true;
      }
    } else {
      if(running) {
        saveItems();
        clearItems();
        running = false;
      }
    }
  }

  @EventHandler
  public void InventoryInteract(InventoryClickEvent event) {
    if(event.getInventory()!=inventoryInterface || event.getRawSlot()!=DRIVE_SLOT) return;
    if(PICKUP_ACTIONS.contains(event.getAction())) {
      if(CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_DRIVE)){
        saveItems();
        event.setCurrentItem(drive);
      }
    }
    if(PLACE_ACTIONS.contains(event.getAction())) {
      drive = event.getCursor();
      loadItems();
    }
  }

  private void saveItems() {
    List<ItemStack> items = new ArrayList<>();
    for(int slot : CONTENT_SLOTS) {
      items.add(inventoryInterface.getItem(slot));
    }
    drive = StorageDrive.saveItemstoDrive(drive, items);
  }

  private void loadItems() {
    if(!running) return;
    if(CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_DRIVE)){
      List<ItemStack> items = StorageDrive.getItemsFromDrive(drive);
      for(int i = 0; i < items.size(); i++) {
        inventoryInterface.setItem(CONTENT_SLOTS[i],items.get(i));
      }
    }
  }

  private void clearItems() {
    for(int slot : CONTENT_SLOTS) {
      inventoryInterface.setItem(slot, new ItemStack(Material.AIR));
    }
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.CELL_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    if (inputSlots.isEmpty()) {
      inputSlots.add(0, new ItemStack(Material.AIR));
    }
    this.inventoryInterface = inventory;
  }

  @Override
  protected void processComplete() {
    // Not used
  }

  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {
    // Not used
  }

}
