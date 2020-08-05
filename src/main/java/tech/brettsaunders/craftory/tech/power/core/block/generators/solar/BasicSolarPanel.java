/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.generators.solar;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseSolarGenerator;

public class BasicSolarPanel extends BaseSolarGenerator {

  /* Static Constants Private */
  private static final byte C_LEVEL = 0;

  /* Construction */
  public BasicSolarPanel(Location location) {
    super(location, Blocks.BASIC_SOLAR_PANEL, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public BasicSolarPanel() {
    super();
  }
}
