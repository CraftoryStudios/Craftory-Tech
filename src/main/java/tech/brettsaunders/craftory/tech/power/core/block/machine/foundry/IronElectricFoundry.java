/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;

public class IronElectricFoundry extends BaseFoundry {

  /* Static Constants Private */
  private static final byte C_LEVEL = 0;

  /* Construction */
  public IronElectricFoundry(Location location) {
    super(location, Blocks.IRON_ELECTRIC_FOUNDRY, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public IronElectricFoundry() {
    super();
  }

}
