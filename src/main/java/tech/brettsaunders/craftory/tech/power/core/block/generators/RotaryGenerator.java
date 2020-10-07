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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.items.CustomTag;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOutputConfig;

public class RotaryGenerator extends BaseGenerator {

  protected static final int maxOutput = 75;
  protected static final int[] MULTIPLIERS = {1, 2, 3, 4};
  protected static final int BASE_CAPACITY = 100000;
  private static final byte C_LEVEL = 0;
  private static final int SLOT = 22;
  protected static List<BlockFace> validFaces = Arrays
      .asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

  static {
    inputFaces = new HashMap<BlockFace, Integer>() {
      {
        put(BlockFace.NORTH, SLOT);
        put(BlockFace.EAST, SLOT);
        put(BlockFace.SOUTH, SLOT);
        put(BlockFace.WEST, SLOT);
        put(BlockFace.UP, SLOT);
      }
    };
  }

  @Persistent
  protected BlockFace facing;
  @Persistent
  protected String modeSaved;
  protected WheelMode mode;
  @Persistent
  @Getter
  protected Boolean wheelPlaced = false;
  protected boolean wheelFree = false;
  protected ArmorStand wheel;
  protected double efficiencyMultiplier = 1;
  protected List<Location> wheelLocations = new ArrayList<>();
  protected List<Location> wheelFootprint = new ArrayList<>();
  @Persistent
  @Getter
  protected Location wheelLocation;

  public RotaryGenerator(Location location) {
    super(location, Blocks.ROTARY_GENERATOR, C_LEVEL, (int) (maxOutput*1.5f),
        BASE_CAPACITY * MULTIPLIERS[C_LEVEL]);
    setFacing(BlockFace.NORTH);
    checkWheel();
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    mode = WheelMode.WATER;
    init();
  }

  public RotaryGenerator() {
    super();
    init();
  }

  private void init() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0, SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    removeWheels();
    modeSaved = mode.toString();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeWheels();
    if (modeSaved != null) {
      mode = WheelMode.valueOf(modeSaved);
    }
    setFacing(facing);
  }

  protected void removeWheels() {
    if (wheel != null) {
      wheel.remove();
      wheelPlaced = false;
    }

  }

  protected void placeWheels() {
    if (wheelPlaced) {
      wheelPlaced = placeWheel(wheelLocation);
    }
  }

  @Override
  public void blockBreak() {
    super.blockBreak();
    removeWheels();
  }


  @Override
  public void updateGenerator() {
    if (!wheelPlaced && wheelFree && inventoryInterface.getItem(SLOT) != null) {
      ItemStack itemStack = inventoryInterface.getItem(SLOT);
      if (CustomItemManager.isCustomItem(itemStack, false)) {
        if (CustomItemManager.matchCustomItemTag(itemStack,
            CustomTag.WINDMILL)) {
          mode = WheelMode.WIND;
          wheelPlaced = true;
          checkWheel();
          if (wheelFree) {
            wheelPlaced = placeWheel(wheelLocation);
          } else {
            wheelPlaced = false;
          }
        } else if (CustomItemManager.matchCustomItemTag(itemStack,
            CustomTag.WATERWHEEL)) {
          mode = WheelMode.WATER;
          wheelPlaced = true;
          checkWheel();
          if (wheelFree) {
            wheelPlaced = placeWheel(wheelLocation);
          } else {
            wheelPlaced = false;
          }
        }
      }
    }
    super.updateGenerator();
  }

  public boolean placeItemIn(ItemStack itemStack) {
    if (wheelPlaced) {
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
      removeWheels();
      return false;
    }
    if ((mode.equals(WheelMode.WATER) && CustomItemManager.matchCustomItemTag(itemStack,
        CustomTag.WATERWHEEL)) || mode.equals(WheelMode.WIND) && CustomItemManager
        .matchCustomItemTag(itemStack,
            CustomTag.WINDMILL)) {
      if (!wheelPlaced) {
        return false;
      }
      if (energyStorage.isFull()) {
        return false;
      }
      return wheelFree;
    }
    removeWheels();
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

  protected boolean placeWheel(Location loc) {
    Location spawnLoc = loc.clone();
    switch (facing) {
      default:
      case NORTH:
        spawnLoc.add(0.5, -0.95, 0.7);
        spawnArmourStand(spawnLoc);
        break;
      case EAST:
        spawnLoc.add(0.3, -0.95, 0.5).setYaw(90);
        spawnArmourStand(spawnLoc);
        break;
      case SOUTH:
        spawnLoc.add(0.5, -0.95, 0.3).setYaw(180);
        spawnArmourStand(spawnLoc);
        break;
      case WEST:
        spawnLoc.add(0.7, -0.95, 0.5).setYaw(270);
        spawnArmourStand(spawnLoc);
        break;
    }
    return true;
  }

  private void spawnArmourStand(Location spawnLoc) {
    if (checkArmourStand(spawnLoc)) {
      return;
    }
    wheel = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
    wheel.setArms(false);
    wheel.setBasePlate(false);
    wheel.setVisible(false);
    wheel.setInvulnerable(true);
    wheel.setGravity(false);
    wheel.setAI(false);
    wheel.setSilent(true);
    wheel.setMarker(true);
    wheel.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(180), 0));
    EntityEquipment entityEquipment = wheel.getEquipment();
    entityEquipment.setHelmet(inventoryInterface.getItem(SLOT));
  }

  private boolean checkArmourStand(Location location) {
    for (Entity entity: location.getChunk().getEntities()) {
      if (entity instanceof ArmorStand && location.distanceSquared(entity.getLocation()) < 0.4D && isGenArmour((ArmorStand) entity)) {
        return true;
      }
    }
    return false;
  }

  private boolean isGenArmour(@NonNull ArmorStand armorStand) {
    return armorStand.isSilent() && !armorStand.hasGravity() && armorStand.isMarker() && armorStand.isInsideVehicle();
  }

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced() * MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
    wheel.setHeadPose(wheel.getHeadPose().add(0, 0, efficiencyMultiplier * 0.1));
  }

  protected int calculateAmountProduced() {
    int temp = (int) Math.round(maxOutput * efficiencyMultiplier);
    return temp;
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
    wheelFootprint.forEach(loc -> {
      locations.add(loc.clone());
    });
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
    wheelFootprint.forEach(loc -> {
      locations.add(loc.clone());
    });
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

  public boolean setFacing(BlockFace face) {
    if (validFaces.contains(face)) {
      facing = face;
      Location loc = location.clone();
      switch (facing) {
        default:
        case NORTH:
          wheelLocation = loc.add(0, 0, -1);
          break;
        case SOUTH:
          wheelLocation = loc.add(0, 0, 1);
          break;
        case EAST:
          wheelLocation = loc.add(1, 0, 0);
          break;
        case WEST:
          wheelLocation = loc.add(-1, 0, 0);
          break;
      }
      wheelLocations = getWheelLocations(wheelLocation);
      wheelFootprint = getWheelFootprint(wheelLocation);
      checkWheel();
      return wheelFree;
    }
    return false;
  }

  @Ticking(ticks = 20)
  public void checkWheel() {
    if (!wheelPlaced) {
      wheelFree = windWheelAreaFree(wheelLocation) || waterWheelAreaFree(wheelLocation);
    } else if (mode.equals(WheelMode.WATER)) {
      wheelFree = waterWheelAreaFree(wheelLocation);
    } else {
      wheelFree = windWheelAreaFree(wheelLocation);
    }

  }

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

  protected List<Location> getWheelFootprint(Location centerLoc) {
    return getLocations(centerLoc, 1, 3);
  }

  protected boolean windWheelAreaFree(Location centerLoc) {
    wheelFree = false;
    for (Location loc : wheelLocations) {
      if (!loc.getBlock().getType().equals(Material.AIR)) {
        return false;
      }
    }
    wheelFree = true;
    return true;
  }

  protected boolean waterWheelAreaFree(Location centerLoc) {
    wheelFree = false;
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
    wheelFree = true;
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
