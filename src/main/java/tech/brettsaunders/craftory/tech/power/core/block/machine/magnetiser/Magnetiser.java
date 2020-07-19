package tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMagnetiser;

public class Magnetiser extends BaseMagnetiser {
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;

  /* Construction */
  public Magnetiser(Location location) {
    super(location, Blocks.MAGNETISER, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public Magnetiser() {
    super();
  }
}
