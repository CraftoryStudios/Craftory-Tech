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
 * Capacity: 400,000 Max Input: 200 Max Output: 200 Level: 0 (IRON)
 */
public class IronCell extends BaseCell {

  /* Static Constants Private */
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 200;

  /* Construction */
  public IronCell(Location location, Player p) {
    super(location, Blocks.IRON_CELL, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public IronCell() {
    super();
  }

}
