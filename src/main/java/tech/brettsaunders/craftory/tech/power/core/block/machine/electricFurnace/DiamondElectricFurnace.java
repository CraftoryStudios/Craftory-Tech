package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class DiamondElectricFurnace extends BaseElectricFurnace {
  private static final long serialVersionUID = 10016L;
  private static final byte CLEVEL = 2;

  public DiamondElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public DiamondElectricFurnace() {
    super();
  }

}
