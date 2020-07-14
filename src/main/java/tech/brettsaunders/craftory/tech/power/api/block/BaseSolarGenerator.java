package tech.brettsaunders.craftory.tech.power.api.block;


import org.bukkit.Location;
import org.bukkit.World.Environment;
import tech.brettsaunders.craftory.Utilities;

public class BaseSolarGenerator extends BaseGenerator{

  private static final int BASE_CAPACITY = 50000;
  private static final int BASE_OUTPUT = 20;
  private static final int[] MULTIPLIERS = {1,4,16,32};
  static final boolean solarDuringStorm = Utilities.config.getBoolean("generators.solarDuringStorms");
  public BaseSolarGenerator(Location location, String blockName, byte level) {
    super(location,blockName,level,BASE_OUTPUT*MULTIPLIERS[level],BASE_CAPACITY*MULTIPLIERS[level]);
  }
  public BaseSolarGenerator() {super();}

  @Override
  protected boolean canStart() {
    if(!location.getWorld().getEnvironment().equals(Environment.NORMAL)) return false;
    if(solarDuringStorm) return location.getWorld().getTime() < 13000 && location.clone().add(0,1,1).getBlock().getLightFromSky()==15;
    return !location.getWorld().isThundering() && location.getWorld().getTime() < 13000 && location.clone().add(0,1,1).getBlock().getLightFromSky()==15;

  }

  @Override
  protected boolean canFinish() {
    return !canStart();
  }

  @Override
  protected void processTick() {
    energyProduced = BASE_OUTPUT*MULTIPLIERS[level];
    energyStorage.modifyEnergyStored(energyProduced);
  }

}