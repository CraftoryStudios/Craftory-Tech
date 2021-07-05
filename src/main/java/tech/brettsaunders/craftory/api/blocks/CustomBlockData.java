/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
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
