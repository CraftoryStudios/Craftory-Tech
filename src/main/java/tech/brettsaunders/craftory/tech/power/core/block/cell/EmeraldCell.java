/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;

/**
 * Energy Cell
 * <p>
 * Capacity: 80,000,000 Max Input: 32,000 Max Output: 32,000 Level: 3 (Emerald)
 */
public class EmeraldCell extends BaseCell {

  /* Static Constants Private */
  private static final byte C_LEVEL = 3;
  private static final int C_OUTPUT_AMOUNT = 32000;

  /* Construction */
  public EmeraldCell(Location location) {
    super(location, Blocks.EMERALD_CELL, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public EmeraldCell() {
    super();
  }

}
