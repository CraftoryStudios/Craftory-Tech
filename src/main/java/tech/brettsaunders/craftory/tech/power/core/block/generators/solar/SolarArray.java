/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.generators.solar;

import org.bukkit.Location;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseSolarGenerator;

public class SolarArray extends BaseSolarGenerator {

  /* Static Constants Private */
  private static final byte C_LEVEL = 3;

  /* Construction */
  public SolarArray(Location location) {
    super(location, Blocks.SOLAR_ARRAY, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public SolarArray() {
    super();
  }
}
