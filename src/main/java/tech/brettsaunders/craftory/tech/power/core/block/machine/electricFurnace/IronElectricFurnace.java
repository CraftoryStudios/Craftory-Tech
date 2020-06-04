package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class IronElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final long serialVersionUID = 10019L;
  private static final byte C_LEVEL = 0;

  /* Construction */
  public IronElectricFurnace(Location location) {
    super(location, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public IronElectricFurnace() {
    super();
  }

}
