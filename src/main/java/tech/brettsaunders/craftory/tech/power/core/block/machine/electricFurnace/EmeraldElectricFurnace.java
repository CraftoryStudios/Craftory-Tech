package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class EmeraldElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final long serialVersionUID = 10017L;
  private static final byte C_LEVEL = 3;

  /* Construction */
  public EmeraldElectricFurnace(Location location) {
    super(location, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public EmeraldElectricFurnace() {
    super();
  }

}
