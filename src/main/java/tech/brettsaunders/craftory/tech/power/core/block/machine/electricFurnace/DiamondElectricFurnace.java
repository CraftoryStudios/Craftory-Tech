package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class DiamondElectricFurnace extends BaseElectricFurnace {

  private static final byte CLEVEL = 3;

  public DiamondElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public DiamondElectricFurnace() {
    super();
  }

}
