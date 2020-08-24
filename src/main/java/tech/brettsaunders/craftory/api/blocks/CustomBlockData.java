/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import lombok.Setter;
import tech.brettsaunders.craftory.api.blocks.tools.ToolLevel;

public class CustomBlockData {

  public final boolean UP;
  public final boolean DOWN;
  public final boolean NORTH;
  public final boolean EAST;
  public final boolean SOUTH;
  public final boolean WEST;
  @Setter
  public ToolLevel BREAK_LEVEL;

  public CustomBlockData(boolean up, boolean down, boolean north, boolean east, boolean south,
      boolean west) {
    this.UP = up;
    this.DOWN = down;
    this.NORTH = north;
    this.EAST = east;
    this.SOUTH = south;
    this.WEST = west;
    this.BREAK_LEVEL = ToolLevel.HAND;
  }
}
