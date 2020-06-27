package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class GoldElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final byte C_LEVEL = 1;

  /* Construction */
  public GoldElectricFurnace(Location location) {
    super(location, Blocks.GOLD_ELECTRIC_FURNACE, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public GoldElectricFurnace() {
    super();
  }

}
