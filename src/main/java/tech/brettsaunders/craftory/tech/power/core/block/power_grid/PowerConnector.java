/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.power_grid;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class PowerConnector extends CustomBlock {

  public PowerConnector() {
    super();
  }

  public PowerConnector(Location location) {
    super(location, Blocks.POWER_CONNECTOR);
  }
}
