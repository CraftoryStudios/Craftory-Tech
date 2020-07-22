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
import java.util.List;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.utils.Logger;

public abstract class BaseRenewableGenerator extends BaseGenerator {

  @Persistent
  protected BlockFace facing;

  @Persistent
  @Getter
  protected boolean wheelPlaced;
  protected boolean wheelFree = false;

  protected static final int maxOutput = 75;
  protected static final int[] MULTIPLIERS = {1,2,3,4};
  protected static final int BASE_CAPACITY = 100000;
  protected double efficiencyMultiplier = 1;
  protected List<Location> wheelLocations = new ArrayList<>();
  @Persistent
  @Getter
  protected Location wheelLocation;

  protected static List<BlockFace> validFaces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

  public BaseRenewableGenerator(Location location, String blockName, byte level) {
    super(location,blockName,level, BASE_CAPACITY,BASE_CAPACITY*MULTIPLIERS[level]);
    setFacing(BlockFace.NORTH);
    checkWheel();
  }
  public BaseRenewableGenerator() {super();}



  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    removeWheels();
  }

  protected abstract void  removeWheels();
  protected abstract void placeWheels();

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeWheels();
  }


  @Override
  protected boolean canStart() {
    if(!wheelPlaced) return false;
    if(energyStorage.isFull()) return false;
    if(!wheelFree) return false;
    return true;
  }

  @Override
  protected void processStart(){
    super.processStart();
    updateEfficiency();
  }

  @Override
  protected boolean canFinish() { return !canStart(); }

  protected abstract boolean placeWheel(Location loc);

  @Override
  protected void processTick() {
    energyProduced = calculateAmountProduced()*MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

  protected int calculateAmountProduced() {
    return (int) Math.round(maxOutput*efficiencyMultiplier);
  }

  @Ticking(ticks=600)
  public abstract void updateEfficiency();

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
      return true;
    }
    return false;
  }

  @Ticking(ticks=20)
  public void checkWheel() {
    wheelFree = wheelAreaFree(wheelLocation);
  }

  protected List<Location> getWheelLocations(Location centerLoc) {
    ArrayList<Location> locations = new ArrayList<>();
    locations.add(centerLoc.clone().add(0,1,0));
    locations.add(centerLoc.clone().add(0,-1,0));
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) { //Ignore z axis
      locations.add(centerLoc.clone().add(1,0,0));
      locations.add(centerLoc.clone().add(-1,0,0));
    } else if(facing.equals(BlockFace.EAST) || facing.equals(BlockFace.WEST)) {
      locations.add(centerLoc.clone().add(0,0,1));
      locations.add(centerLoc.clone().add(0,0,-1));
    } else {
      Logger.warn("Renewable Generator set to invalid facing direction");
    }
    return locations;
  }

  protected boolean wheelAreaFree(Location centerLoc) {
    wheelFree = false;
    for(Location loc: wheelLocations) {
      if(!loc.getBlock().getType().equals(Material.AIR)) return false;
    }
    wheelFree = true;
    return true;
  }
}
