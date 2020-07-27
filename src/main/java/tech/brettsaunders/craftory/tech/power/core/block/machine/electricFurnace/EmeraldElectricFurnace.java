/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class EmeraldElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final byte C_LEVEL = 3;

  /* Construction */
  public EmeraldElectricFurnace(Location location) {
    super(location, Blocks.EMERALD_ELECTRIC_FURNACE, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public EmeraldElectricFurnace() {
    super();
  }

}
