package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;

public class EmeraldElectricFoundry extends BaseFoundry {

  /* Static Constants Private */
  private static final byte C_LEVEL = 3;

  /* Construction */
  public EmeraldElectricFoundry(Location location) {
    super(location, Blocks.EMERALD_ELECTRIC_FOUNDRY, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public EmeraldElectricFoundry() {
    super();
  }

}
