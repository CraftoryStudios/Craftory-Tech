/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Constants.Blocks;
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
  public EmeraldCell(Location location, Player p) {
    super(location, Blocks.EMERALD_CELL, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public EmeraldCell() {
    super();
  }

}
