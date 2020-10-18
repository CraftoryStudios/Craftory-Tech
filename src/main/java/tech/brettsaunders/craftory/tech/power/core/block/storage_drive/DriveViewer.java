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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.storage_drive.StorageDrive;

public class DriveViewer extends BaseMachine {

  private static final int DRIVE_SLOT = 15;
  private static final List<Integer> CONTENT_SLOTS = new ArrayList<>(Arrays.asList(16,24,25,33,34,42,43));
  private static final Set<InventoryAction> PLACE_ACTIONS = new HashSet<>(Arrays
      .asList(InventoryAction.PLACE_ALL,
          InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR));
  private static final Set<InventoryAction> PICKUP_ACTIONS = new HashSet<>(Arrays
      .asList(InventoryAction.PICKUP_ALL,
          InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.SWAP_WITH_CURSOR));

  private ItemStack drive;

  private boolean running = false;
  private boolean loaded = false;

  public DriveViewer(Location location) {
    super(location, Blocks.DRIVE_VIEWER, (byte) 0, 200);
    setup();
    energyStorage = new EnergyStorage(10000);
  }

  public DriveViewer() {
    super();
    setup();
  }

  @Override
  public void beforeSaveUpdate() {
    if(drive!=null && CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_STORAGE_DRIVE)){
      saveItems();
      inventoryInterface.setItem(DRIVE_SLOT, drive);
    }
    super.beforeSaveUpdate();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    running = false;
  }

  @Override
  public void blockBreak() {
    saveItems();
    inputSlots.set(0,drive);
    clearItems();
    super.blockBreak();
  }

  private void setup() {
    outputLocations = new ArrayList<>();
    inputLocations = new ArrayList<>();
    inputLocations.add(DRIVE_SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(DRIVE_SLOT));
    interactableSlots.addAll(CONTENT_SLOTS);
    this.energyConsumption = 10;
  }

  /* Update Loop */
  @Ticking(ticks = 1)
  @Override
  public void updateMachine() {
    if(inventoryInterface==null) return;
    drive = inventoryInterface.getItem(DRIVE_SLOT);
    inputSlots.set(0, drive);
    if(drive==null || drive.getType().equals(Material.AIR)) return;
    if(inventoryInterface.getViewers().isEmpty() || !CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_STORAGE_DRIVE)) return;
    if(hasSufficientEnergy()) {
      if(!running) {
        running = true;
        loadItems();
      }
    } else {
      if(running) {
        saveItems();
        running = false;
      }
    }
  }

  @EventHandler
  public void inventoryInteract(InventoryClickEvent event) {
    if(event.getInventory()!=inventoryInterface || !running) return;
    if(event.isShiftClick()){
      handleShiftClick(event);
    } else if(event.getRawSlot()==DRIVE_SLOT){
    handleDriveSlotClick(event);
    } else if (CONTENT_SLOTS.contains(event.getRawSlot()) && PLACE_ACTIONS.contains(event.getAction())){
      handleContentClick(event);
    }
  }

  private void handleContentClick(InventoryClickEvent event) {
    if(CustomItemManager.getCustomItemName(event.getCursor()).equals(Items.BASIC_STORAGE_DRIVE)) {
      event.setCancelled(true);
    }
  }
  private void handleShiftClick(InventoryClickEvent event) {
    if(event.getRawSlot()==DRIVE_SLOT){
      saveItems();
      event.setCurrentItem(drive);
    } else if(event.getRawSlot() > 57 && inventoryInterface.getItem(DRIVE_SLOT)==null){
      drive = event.getCurrentItem();
      loadItems();
      inventoryInterface.setItem(DRIVE_SLOT, drive);
      event.setCancelled(true);
    }
    refreshInventories();
  }

  private void handleDriveSlotClick(InventoryClickEvent event) {
    if(PICKUP_ACTIONS.contains(event.getAction()) && CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_STORAGE_DRIVE)) {
      saveItems();
      event.setCurrentItem(drive);
    }
    if(PLACE_ACTIONS.contains(event.getAction())) {
      drive = event.getCursor();
      loadItems();
    }
    refreshInventories();
  }

  private void saveItems() {
    if(drive==null || !loaded) return;
    List<ItemStack> items = new ArrayList<>();
    for(int slot : CONTENT_SLOTS) {
      ItemStack itemStack = inventoryInterface.getItem(slot);
      if(itemStack!=null && itemStack.getType()!=Material.AIR){
        items.add(itemStack);
      }

    }
    drive = StorageDrive.saveItemstoDrive(drive, items);
    clearItems();
  }

  private void loadItems() {
    if(loaded) return;
    if(CustomItemManager.getCustomItemName(drive).equals(Items.BASIC_STORAGE_DRIVE)){
      List<ItemStack> items = StorageDrive.getItemsFromDrive(drive);
      for(int i = 0; i < items.size(); i++) {
        inventoryInterface.setItem(CONTENT_SLOTS.get(i),items.get(i));
      }
    }
    interactableSlots.removeAll(CONTENT_SLOTS);
    interactableSlots.addAll(CONTENT_SLOTS);
    loaded = true;
  }

  private void refreshInventories() {
    Tasks.runTaskLater(() -> {
      for(HumanEntity viewer: inventoryInterface.getViewers()) {
        if(viewer instanceof Player) ((Player) viewer).updateInventory();
      }
    },1);
  }

  private void clearItems() {
    for(int slot : CONTENT_SLOTS) {
      inventoryInterface.setItem(slot, new ItemStack(Material.AIR));
    }
    interactableSlots.removeAll(CONTENT_SLOTS);
    loaded = false;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.DRIVE_VIEWER.label + "");
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
