package tech.brettsaunders.craftory.tech.power.core.block.generators;

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

  protected static final int maxOutput = 100;
  protected static final int[] MULTIPLIERS = {1,2,3,4};
  protected static final int BASE_CAPACITY = 10000;
  protected int efficiencyMultiplier = 1;
  @Persistent
  @Getter
  protected Location wheelLocation;

  protected static List<BlockFace> validFaces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

  public BaseRenewableGenerator(Location location, String blockName, byte level) {
    super(location,blockName,level, maxOutput*MULTIPLIERS[level],BASE_CAPACITY*MULTIPLIERS[level]);
  }
  public BaseRenewableGenerator() {super();}


  @Override
  protected boolean canStart() {
    if(!wheelPlaced) return false;
    if(energyStorage.isFull()) return false;
    if(!wheelFree) return false;
    updateAmountGenerated();
    return true;
  }

  @Override
  protected boolean canFinish() { return !canStart(); }

  @Override
  protected void processTick() {
    energyProduced = maxOutput*efficiencyMultiplier*MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

  @Ticking(ticks=100)
  public abstract void updateAmountGenerated();

  protected boolean setFacing(BlockFace face) {
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
      return true;
    }
    return false;
  }

  @Ticking(ticks=100)
  protected void checkWheel() {
    if(!wheelPlaced) return;
    wheelFree = wheelAreaFree(wheelLocation);
  }

  @Ticking(ticks=100)
  protected boolean wheelAreaFree(Location centerLoc) {
    wheelFree = false;
    Location loc = centerLoc.clone();
    if(!centerLoc.getBlock().getType().equals(Material.AIR)) return false; //TODO if it's the wheel block dont return false
    if(!loc.add(0,1,0).getBlock().getType().equals(Material.AIR)) return false;
    loc = centerLoc.clone();
    if(!loc.add(0,-1,0).getBlock().getType().equals(Material.AIR)) return false;
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) { //Ignore z axis
      loc = centerLoc.clone();
      if(!loc.add(1,0,0).getBlock().getType().equals(Material.AIR)) return false;
      loc = centerLoc.clone();
      if(!loc.add(-1,0,0).getBlock().getType().equals(Material.AIR)) return false;
    } else if(facing.equals(BlockFace.EAST) || facing.equals(BlockFace.WEST)) {
      loc = centerLoc.clone();
      if(!loc.add(0,0,1).getBlock().getType().equals(Material.AIR)) return false;
      loc = centerLoc.clone();
      if(!loc.add(0,0,-1).getBlock().getType().equals(Material.AIR)) return false;
    } else {
      Logger.warn("Renewable Generator set to invalid facing direction");
    }
    wheelFree = true;
    return true;
  }
}
