package tech.brettsaunders.craftory.tech.power.core.block.machine.generators.solar;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseSolarGenerator;

public class CompactedSolarPanel extends BaseSolarGenerator {
  /* Static Constants Private */
  private static final byte C_LEVEL = 2;

  /* Construction */
  public CompactedSolarPanel(Location location) {
    super(location, Blocks.COMPACTED_SOLAR_PANEL, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public CompactedSolarPanel() {
    super();
  }
}
