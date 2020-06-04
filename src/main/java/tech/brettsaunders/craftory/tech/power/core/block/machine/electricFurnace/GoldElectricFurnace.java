package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class GoldElectricFurnace extends BaseElectricFurnace {
  private static final long serialVersionUID = 10018L;
  private static final byte CLEVEL = 1;

  public GoldElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public GoldElectricFurnace() {
    super();
  }

}
