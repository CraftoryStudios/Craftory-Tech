/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.power_grid;

import org.bukkit.Location;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class PowerConnector extends CustomBlock {

  public PowerConnector() {
    super();
  }

  public PowerConnector(Location location) {
    super(location, Blocks.POWER_CONNECTOR);
  }
}
