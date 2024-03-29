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
 * Capacity: 2,000,000 Max Input: 800 Max Output: 800 Level: 1 (GOLD)
 */
public class GoldCell extends BaseCell {

  /* Static Constants Private */
  private static final byte C_LEVEL = 1;
  private static final int C_OUTPUT_AMOUNT = 800;

  /* Construction */
  public GoldCell(Location location, Player p) {
    super(location, Blocks.GOLD_CELL, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public GoldCell() {
    super();
  }

}
