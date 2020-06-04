package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class IronElectricFurnace extends BaseElectricFurnace {
  private static final long serialVersionUID = 10019L;
  private static final byte CLEVEL = 0;

  public IronElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public IronElectricFurnace() {
    super();
  }

}
