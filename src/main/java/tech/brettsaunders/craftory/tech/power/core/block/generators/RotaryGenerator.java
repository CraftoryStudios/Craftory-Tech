/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.items.CustomTag;
import tech.brettsaunders.craftory.packet_wrapper.Angle;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.core.utils.ArmourStandUtils;

//TODO on new player joins
public class RotaryGenerator extends BaseGenerator {

  protected static final int MAX_OUTPUT = 75;
  protected static final int[] MULTIPLIERS = {1, 2, 3, 4};
  protected static final int BASE_CAPACITY = 100000;
  private static final byte C_LEVEL = 0;
  private static final int SLOT = 22;
  protected static List<BlockFace> validFaces = Arrays
      .asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

  static {
    inputFaces.put(BlockFace.NORTH, SLOT);
    inputFaces.put(BlockFace.EAST, SLOT);
    inputFaces.put(BlockFace.SOUTH, SLOT);
    inputFaces.put(BlockFace.WEST, SLOT);
    inputFaces.put(BlockFace.UP, SLOT);
  }

  @Persistent
  @Getter
  protected BlockFace facing;
  @Persistent
  protected String modeSaved;
  @Persistent
  @Getter
  protected Location wheelLocation;

  protected WheelMode mode;
  protected double efficiencyMultiplier = 1;
  protected List<Location> wheelLocations = new ArrayList<>();
  protected List<Location> wheelFootprint = new ArrayList<>();
  protected boolean placementSpace = false;
  @Getter
  protected int entityID = (int)(Math.random() * Integer.MAX_VALUE);
  @Getter
  protected UUID uuid = UUID.randomUUID();
  Angle headAngle = new Angle();
  boolean holding = false;

  public RotaryGenerator(Location location) {
    super(location, Blocks.ROTARY_GENERATOR, C_LEVEL, (int) (MAX_OUTPUT *1.5f),
        BASE_CAPACITY * MULTIPLIERS[C_LEVEL]);
    //Setup defaults
    mode = WheelMode.WIND;
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    init();
    //Places Armourstand
    setFacing(BlockFace.NORTH);
  }

  public RotaryGenerator() {
    super();
    init();
  }

  private void init() {
    ArmourStandUtils.register(this);
    //Setup GUI
    inputLocations = new ArrayList<>();
    inputLocations.add(0, SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    modeSaved = mode.toString();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    if (modeSaved != null) {
      mode = WheelMode.valueOf(modeSaved);
    }
    //Places Armourstand
    setFacing(facing);
  }

  public boolean setFacing(BlockFace face) {
    if (!validFaces.contains(face))  return false;

    //Set new direction
    facing = face;

    //Update wheel location
    wheelLocation = centerLocation(location.clone(), face);
    wheelLocations = getWheelLocations(wheelLocation);
    wheelFootprint = getWheelFootprint(wheelLocation);

    if (checkPlacementSpace()) {
      ArmourStandUtils.destroyArmourStand(entityID, false);
      ArmourStandUtils.spawnArmourStand(wheelLocation, entityID, uuid, facing, new HashSet<>(Bukkit.getOnlinePlayers()));
      return true;
    }
    return false;
  }

  private boolean isItemSet() {
    ItemStack item = inventoryInterface.getItem(SLOT);
    if (item == null) return false;
    //TODO improve this check
    return item.getType() == Material.PAPER;
  }

  private Location centerLocation(Location location, BlockFace face) {
    switch (face) {
      default:
      case NORTH:
        return location.add(0, 0, -1);
      case SOUTH:
        return location.add(0, 0, 1);
      case EAST:
        return location.add(1, 0, 0);
      case WEST:
        return location.add(-1, 0, 0);
    }
  }


  @Override
  public void blockBreak() {
    super.blockBreak();
    ArmourStandUtils.destroyArmourStand(entityID, true);
  }

  public ItemStack getArmourStandItem() {
    ItemStack itemStack = inventoryInterface.getItem(SLOT);
    if (itemStack != null && CustomItemManager.isCustomItem(itemStack, false)) {
        checkPlacementSpace();
        //Windmill
        if (placementSpace && CustomItemManager.matchCustomItemTag(itemStack,
            CustomTag.WINDMILL)) {
          mode = WheelMode.WIND;
          return itemStack;
        }
        //Waterwheel
        else if (placementSpace && CustomItemManager.matchCustomItemTag(itemStack,
            CustomTag.WATERWHEEL)) {
          mode = WheelMode.WATER;
          return itemStack;
        }
      }
    return new ItemStack(Material.AIR);
  }

  @Override
  public void updateGenerator() {
    if (holding && !isItemSet()) holding = false;
    if (!holding && placementSpace) {
      ArmourStandUtils.setEntityHolding(entityID, getArmourStandItem(), Bukkit.getOnlinePlayers());
      holding = true;
    }
    super.updateGenerator();
  }

  public boolean placeItemIn(ItemStack itemStack) {
    if (holding) {
      return false;
    }
    if (inventoryInterface.getItem(SLOT) == null || inventoryInterface.getItem(SLOT).getType()
        .equals(Material.AIR)) {
      itemStack.setAmount(1);
      inventoryInterface.setItem(SLOT, itemStack);
      return true;
    }
    if (CustomItemManager.getCustomItemName(itemStack)
        .equals(CustomItemManager.getCustomItemName(inventoryInterface.getItem(SLOT)))) {
      ItemStack slot = inventoryInterface.getItem(SLOT);
      if (slot.getAmount() + 1 >= slot.getMaxStackSize()) {
        return false;
      }
      slot.setAmount(slot.getAmount() + 1);
      return true;
    }
    return false;
  }

  @Override
  protected boolean canStart() {
    ItemStack itemStack = inventoryInterface.getItem(SLOT);
    if (itemStack == null || !CustomItemManager.isCustomItem(itemStack, false)) {
      ArmourStandUtils.setEntityHolding(entityID, new ItemStack(Material.AIR), Bukkit.getOnlinePlayers());
      holding = false;
      return false;
    }
    if ((mode.equals(WheelMode.WATER) && CustomItemManager.matchCustomItemTag(itemStack,
        CustomTag.WATERWHEEL)) || mode.equals(WheelMode.WIND) && CustomItemManager
        .matchCustomItemTag(itemStack,
            CustomTag.WINDMILL)) {
      if (!holding) {
        return false;
      }
      if (!Utilities.config.getBoolean("generators.rotaryGeneratorsSpinWhenFull") && energyStorage.isFull()) {
        return false;
      }
      return true;
    }
    ArmourStandUtils.setEntityHolding(entityID, new ItemStack(Material.AIR), Bukkit.getOnlinePlayers());
    holding = false;
    return false;
  }

  @Override
  protected void processStart() {
    super.processStart();
    updateEfficiency();
  }

  @Override
  protected boolean canFinish() {
    return !canStart();
  }

  @Ticking(ticks=4)
  public void rotateEntity() {
    if (!isActive) return;
    int amount = ((int) Math.toDegrees(efficiencyMultiplier * 0.1)) * 4;
    headAngle.add(amount);
    ArmourStandUtils.rotateEntity(entityID,headAngle.getAngle());
  }

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced() * MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

  protected int calculateAmountProduced() {
    return (int) Math.round(MAX_OUTPUT * efficiencyMultiplier);
  }

  @Ticking(ticks = 600)
  public void updateEfficiency() {
    if (mode.equals(WheelMode.WIND)) {
      updateWindEfficiency();
    } else {
      efficiencyMultiplier = 0.25;
    }
  }

  private void updateWindEfficiency() {
    final ArrayList<Location> locations = new ArrayList<>();
    wheelFootprint.forEach(loc -> locations.add(loc.clone()));
    Vector v;
    if (facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0, 0, 1);
    } else {
      v = new Vector(1, 0, 0);
    }
    int clearBlocks = 0;
    for (Location loc : locations) {
      for (int i = 0; i < 11; i++) {
        if (!loc.add(v).getBlock().getType().equals(Material.AIR)) {
          break;
        }
        clearBlocks += 1;
      }
    }
    locations.clear();
    wheelFootprint.forEach(loc -> locations.add(loc.clone()));
    if (facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0, 0, -1);
    } else {
      v = new Vector(-1, 0, 0);
    }
    for (Location loc : locations) {
      for (int i = 0; i < 11; i++) {
        if (!loc.add(v).getBlock().getType().equals(Material.AIR)) {
          break;
        }
        clearBlocks += 1;
      }
    }
    clearBlocks -= 300;
    if (clearBlocks < 1) {
      clearBlocks = 1;
    }
    efficiencyMultiplier = Math.min(1d, clearBlocks / (1034d - 300));
  }

  @Ticking(ticks = 20)
  public boolean checkPlacementSpace() {
    //TODO Don't check if entity placed
    if (mode.equals(WheelMode.WATER)) {
      placementSpace = waterWheelAreaFree(wheelLocation);
    } else {
      placementSpace = windWheelAreaFree();
    }
    return placementSpace;
  }

  //TODO FIx
  protected List<Location> getWheelLocations(Location centerLoc) {
    return getLocations(centerLoc, 2, 3);
  }

  private List<Location> getLocations(Location centerLoc, int thick, int rad) {
    ArrayList<Location> locations = new ArrayList<>();
    if (facing.equals(BlockFace.NORTH)) {
      for (int x = -rad; x <= rad; x++) {
        for (int y = -rad; y <= rad; y++) {
          for (int z = -thick + 1; z <= 0; z++) {
            locations.add(centerLoc.clone().add(x, y, z));
          }
        }
      }
    } else if (facing.equals(BlockFace.SOUTH)) {
      for (int x = -rad; x <= rad; x++) {
        for (int y = -rad; y <= rad; y++) {
          for (int z = 0; z < thick; z++) {
            locations.add(centerLoc.clone().add(x, y, z));
          }
        }
      }
    } else if (facing.equals(BlockFace.EAST)) {
      for (int x = 0; x < thick; x++) {
        for (int y = -rad; y <= rad; y++) {
          for (int z = -rad; z <= rad; z++) {
            locations.add(centerLoc.clone().add(x, y, z));
          }
        }
      }
    } else if (facing.equals(BlockFace.WEST)) {
      for (int x = -thick + 1; x <= 0; x++) {
        for (int y = -rad; y <= rad; y++) {
          for (int z = -rad; z <= rad; z++) {
            locations.add(centerLoc.clone().add(x, y, z));
          }
        }
      }
    }
    return locations;
  }

  //TODO Fix
  protected List<Location> getWheelFootprint(Location centerLoc) {
    return getLocations(centerLoc, 1, 3);
  }

  protected boolean windWheelAreaFree() {
    placementSpace = false;
    for (Location loc : wheelLocations) {
      if (!loc.getBlock().getType().equals(Material.AIR)) {
        return false;
      }
    }
    placementSpace = true;
    return true;
  }

  protected boolean waterWheelAreaFree(Location centerLoc) {
    placementSpace = false;
    if (!centerLoc.getBlock().getType().equals(Material.AIR)) {
      return false;
    }
    int waterCount = 0;
    for (Location loc : wheelLocations) {
      if (!loc.getBlock().getType().equals(Material.AIR)) {
        if (loc.getBlock().getType().equals(Material.WATER)) {
          waterCount += 1;
        } else {
          return false;
        }
      }
    }
    if (waterCount > 5000 || waterCount < 14) {
      return false;
    }
    placementSpace = true;
    return true;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.GENERATOR_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, 43, true));
    addGUIComponent(new GIndicator(inventory, runningContainer, 31));
    this.inventoryInterface = inventory;
  }

  private enum WheelMode {
    WIND,
    WATER
  }
}
