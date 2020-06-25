package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;

public class IronFoundry extends BaseFoundry {

  /* Static Constants Private */
  private static final long serialVersionUID = 10019L;
  private static final byte C_LEVEL = 0;

  /* Construction */
  public IronFoundry(Location location) {
    super(location, Blocks.IRON_FOUNDRY, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public IronFoundry() {
    super();
  }
}
