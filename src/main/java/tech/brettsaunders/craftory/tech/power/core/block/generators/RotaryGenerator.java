/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.utils.Logger;

public class RotaryGenerator extends BaseGenerator {

  @Persistent
  protected BlockFace facing;
  @Persistent
  protected WheelMode mode;

  @Persistent
  @Getter
  protected boolean wheelPlaced;
  protected boolean wheelFree = false;
  private static final byte C_LEVEL = 0;
  private static final int SLOT = 22;
  protected ArmorStand wheel;
  protected static final int maxOutput = 75;
  protected static final int[] MULTIPLIERS = {1,2,3,4};
  protected static final int BASE_CAPACITY = 100000;
  protected double efficiencyMultiplier = 1;
  protected List<Location> wheelLocations = new ArrayList<>();
  @Persistent
  @Getter
  protected Location wheelLocation;

  protected static List<BlockFace> validFaces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

  public RotaryGenerator(Location location) {
    super(location, Blocks.ROTARY_GENERATOR,C_LEVEL, BASE_CAPACITY,BASE_CAPACITY*MULTIPLIERS[C_LEVEL]);
    setFacing(BlockFace.NORTH);
    checkWheel();
    init();
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    mode = WheelMode.WATER;
  }
  public RotaryGenerator() {super();
    init();}

  private void init() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0,SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
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


  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    removeWheels();
  }

  protected void removeWheels() {
    if (wheel != null){
      wheel.remove();
      wheelPlaced = false;
    }

  }
  protected void placeWheels() {
    if(wheelPlaced){
      wheelPlaced =  placeWheel(wheelLocation);
    }
  }

  @Override
  public void blockBreak() {
    super.blockBreak();
    removeWheels();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeWheels();
  }

  @Override
  public void updateGenerator() {
    if(!wheelPlaced && wheelFree && inventoryInterface.getItem(SLOT)!=null){
      ItemStack itemStack = inventoryInterface.getItem(SLOT);
      if(CustomItemManager.isCustomItem(itemStack, false)) {
        if(CustomItemManager.matchCustomItemName(itemStack,
            Items.WINDMILL)) {
          mode = WheelMode.WIND;
          wheelPlaced = true;
          checkWheel();
          if(wheelFree) {
            wheelPlaced =  placeWheel(wheelLocation);
          } else wheelPlaced = false;
        } else if (CustomItemManager.matchCustomItemName(itemStack,
            Items.WATER_WHEEL)) {
          mode = WheelMode.WATER;
          wheelPlaced = true;
          checkWheel();
          if(wheelFree) {
            wheelPlaced =  placeWheel(wheelLocation);
          } else wheelPlaced = false;
        }
      }
    }
    super.updateGenerator();
  }

  public void placeItemIn(ItemStack itemStack) {
    if(wheelPlaced) return;
    itemStack.setAmount(1);
    inventoryInterface.setItem(SLOT,itemStack);
  }

  @Override
  protected boolean canStart() {
    ItemStack itemStack = inventoryInterface.getItem(SLOT);
    if(itemStack==null || !CustomItemManager.isCustomItem(itemStack, false)) {
      removeWheels();
      return false;
    }
    if((mode.equals(WheelMode.WATER) &&CustomItemManager.matchCustomItemName(itemStack,
        Items.WATER_WHEEL)) || mode.equals(WheelMode.WIND) &&CustomItemManager.matchCustomItemName(itemStack,
        Items.WINDMILL)){
      if(!wheelPlaced) return false;
      if(energyStorage.isFull()) return false;
      if(!wheelFree) return false;
      return true;
    }
    removeWheels();
    return false;
  }

  @Override
  protected void processStart(){
    super.processStart();
    updateEfficiency();
  }

  @Override
  protected boolean canFinish() { return !canStart(); }

  protected boolean placeWheel(Location loc) {
    Location spawnLoc = loc.clone();
    switch (facing) {
      case NORTH:
        spawnLoc.add(0.5,-0.95,0.7);
        spawnArmourStand(spawnLoc, 0);
        break;
      case EAST:
        spawnLoc.add(0.3,-0.95,0.5);
        spawnArmourStand(spawnLoc, 90);
        break;
      case SOUTH:
        spawnLoc.add(0.5,-0.95,0.3);
        spawnArmourStand(spawnLoc, 180);
        break;
      case WEST:
        spawnLoc.add(0.7,-0.95,0.5);
        spawnArmourStand(spawnLoc, 270);
        break;
    }
    return true;
  }

  private void spawnArmourStand(Location spawnLoc, int rotation) {
    wheel = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
    wheel.setArms(false);
    wheel.setBasePlate(false);
    wheel.setVisible(false);
    wheel.setInvulnerable(true);
    wheel.setGravity(false);
    wheel.setAI(false);
    wheel.setMarker(true);
    wheel.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(180), 0));
    EntityEquipment entityEquipment = wheel.getEquipment();
    if(mode.equals(WheelMode.WIND)) entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.WINDMILL));
    else entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.WATER_WHEEL));
    wheel.setRotation(rotation,0);
  }

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced()*MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
    wheel.setHeadPose(wheel.getHeadPose().add(0,0,efficiencyMultiplier*0.1));
  }

  protected int calculateAmountProduced() {
    return (int) Math.round(maxOutput*efficiencyMultiplier);
  }

  @Ticking(ticks=600)
  public  void updateEfficiency() {
    if(mode.equals(WheelMode.WIND)){
      updateWindEfficiency();
    } else {
      efficiencyMultiplier = 0.5;
    }
  }

  private void updateWindEfficiency() {
    final ArrayList<Location> locations = new ArrayList<>();
    locations.add(wheelLocation);
    wheelLocations.forEach(loc -> {
      locations.add(loc.clone());
    });
    Vector v;
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0,0,1);
    } else {
      v = new Vector(1,0,0);
    }
    int maxClearPositive = 12;
    boolean clear = true;
    for (int i = 1; i < 13; i++) {
      for(Location loc: locations) {
        if(!loc.add(v).getBlock().getType().equals(Material.AIR)){
          clear = false;
          break;
        }
      }
      if(!clear){
        maxClearPositive = i;
        break;
      }
    }
    locations.clear();
    locations.add(wheelLocation);
    wheelLocations.forEach(loc -> {
      locations.add(loc.clone());
    });
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0,0,-1);
    } else {
      v = new Vector(-1,0,0);
    }
    int maxClearNegative = 12;
    clear = true;
    for (int i = 2; i < 13; i++) {
      for(Location loc: locations) {
        if(!loc.add(v).getBlock().getType().equals(Material.AIR)){
          clear = false;
          break;
        }
      }
      if(!clear){
        maxClearNegative = i;
        break;
      }
    }
    efficiencyMultiplier = Math.min(maxClearNegative,maxClearPositive)/12d;
  }

  public boolean setFacing(BlockFace face) {
    if(validFaces.contains(face)){
      facing = face;
      Location loc = location.clone();
      switch (facing) {
        case NORTH:
          wheelLocation = loc.add(0,0,-1);
          break;
        case SOUTH:
          wheelLocation = loc.add(0,0,1);
          break;
        case EAST:
          wheelLocation = loc.add(1,0,0);
          break;
        case WEST:
          wheelLocation = loc.add(-1,0,0);
          break;
      }
      wheelLocations = getWheelLocations(wheelLocation);
      checkWheel();
      return true;
    }
    return false;
  }

  @Ticking(ticks=20)
  public void checkWheel() {
    if(!wheelPlaced)
      wheelFree = windWheelAreaFree(wheelLocation) || waterWheelAreaFree(wheelLocation);
    else if(mode.equals(WheelMode.WATER)){
      wheelFree = waterWheelAreaFree(wheelLocation);
    } else {
      wheelFree = windWheelAreaFree(wheelLocation);
    }

  }

  protected List<Location> getWheelLocations(Location centerLoc) {
    ArrayList<Location> locations = new ArrayList<>();
    locations.add(centerLoc.clone().add(0,1,0));
    locations.add(centerLoc.clone().add(0,-1,0));
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) { //Ignore z axis
      locations.add(centerLoc.clone().add(1,0,0));
      locations.add(centerLoc.clone().add(-1,0,0));
      locations.add(centerLoc.clone().add(1,1,0));
      locations.add(centerLoc.clone().add(-1,1,0));
      locations.add(centerLoc.clone().add(1,-1,0));
      locations.add(centerLoc.clone().add(-1,-1,0));
    } else if(facing.equals(BlockFace.EAST) || facing.equals(BlockFace.WEST)) {
      locations.add(centerLoc.clone().add(0,0,1));
      locations.add(centerLoc.clone().add(0,0,-1));
      locations.add(centerLoc.clone().add(0,1,1));
      locations.add(centerLoc.clone().add(0,1,-1));
      locations.add(centerLoc.clone().add(0,-1,1));
      locations.add(centerLoc.clone().add(0,-1,-1));
    } else {
      Logger.warn("Renewable Generator set to invalid facing direction");
    }
    return locations;
  }

  protected boolean windWheelAreaFree(Location centerLoc) {
    wheelFree = false;
    for(Location loc: wheelLocations) {
      if(!loc.getBlock().getType().equals(Material.AIR)) return false;
    }
    wheelFree = true;
    return true;
  }

  protected boolean waterWheelAreaFree(Location centerLoc) {
    wheelFree = false;
    if(!centerLoc.getBlock().getType().equals(Material.AIR)) return false;
    int waterCount = 0;
    for(Location loc: wheelLocations) {
      if(!loc.getBlock().getType().equals(Material.AIR)){
        if(loc.getBlock().getType().equals(Material.WATER)) waterCount +=1;
        else return false;
      }
    }
    if(waterCount > 5 || waterCount < 3) return false;
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
    WATER;
  }
}
