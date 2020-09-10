/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks.basicBlocks;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.bukkit.block.BlockFace;

public enum BasicBlocks {
  COPPER_ORE(true, false, true, true, false, false),
  CRYSTAL_ORE(false, false, true, true, false, false);

  @Getter
  private final HashSet<BlockFace> allowedFaces = new HashSet<BlockFace>();

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
