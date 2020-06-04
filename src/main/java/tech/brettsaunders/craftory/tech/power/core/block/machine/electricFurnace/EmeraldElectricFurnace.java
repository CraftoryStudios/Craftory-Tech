package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class EmeraldElectricFurnace extends BaseElectricFurnace {
  private static final long serialVersionUID = 10017L;
  private static final byte CLEVEL = 3;

  public EmeraldElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public EmeraldElectricFurnace() {
    super();
  }

}
