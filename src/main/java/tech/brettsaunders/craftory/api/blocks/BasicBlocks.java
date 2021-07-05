/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import java.util.HashSet;
import lombok.Getter;
import org.bukkit.block.BlockFace;

public enum BasicBlocks {
  COPPER_ORE(true, false, true, true, false, false),
  CRYSTAL_ORE(false, false, true, true, false, false);

  @Getter
  private final HashSet<BlockFace> allowedFaces = new HashSet<>();

  BasicBlocks(boolean west, boolean up, boolean south, boolean north, boolean east, boolean down) {
    if (west) {
      allowedFaces.add(BlockFace.WEST);
    }
    if (up) {
      allowedFaces.add(BlockFace.UP);
    }
    if (south) {
      allowedFaces.add(BlockFace.SOUTH);
    }
    if (north) {
      allowedFaces.add(BlockFace.NORTH);
    }
    if (east) {
      allowedFaces.add(BlockFace.EAST);
    }
    if (down) {
      allowedFaces.add(BlockFace.DOWN);
    }
  }
}
