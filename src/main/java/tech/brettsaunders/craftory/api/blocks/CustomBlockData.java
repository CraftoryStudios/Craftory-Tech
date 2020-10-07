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

  public final boolean up;
  public final boolean down;
  public final boolean north;
  public final boolean east;
  public final boolean south;
  public final boolean west;
  @Setter
  public ToolLevel breakLevel;

  public CustomBlockData(boolean up, boolean down, boolean north, boolean east, boolean south,
      boolean west) {
    this.up = up;
    this.down = down;
    this.north = north;
    this.east = east;
    this.south = south;
    this.west = west;
    this.breakLevel = ToolLevel.HAND;
  }
}
