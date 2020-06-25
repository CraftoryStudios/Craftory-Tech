package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class DiamondElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final long serialVersionUID = 10016L;
  private static final byte C_LEVEL = 2;

  /* Construction */
  public DiamondElectricFurnace(Location location) {
    super(location, Blocks.DIAMOND_ELECTRIC_FURNACE,  C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public DiamondElectricFurnace() {
    super();
  }

}
