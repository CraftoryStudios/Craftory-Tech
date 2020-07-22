/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.macerator;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMacerator;

public class DiamondMacerator extends BaseMacerator {
  /* Static Constants Private */
  private static final byte C_LEVEL = 0;

  /* Construction */
  public DiamondMacerator(Location location) {
    super(location, Blocks.DIAMOND_MACERATOR, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public DiamondMacerator() {
    super();
  }
}
