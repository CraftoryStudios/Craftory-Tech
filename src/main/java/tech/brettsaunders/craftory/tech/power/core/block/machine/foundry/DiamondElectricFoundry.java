/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.foundry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseFoundry;

public class DiamondElectricFoundry extends BaseFoundry {

  /* Static Constants Private */
  private static final byte C_LEVEL = 2;

  /* Construction */
  public DiamondElectricFoundry(Location location, Player p) {
    super(location, Blocks.DIAMOND_ELECTRIC_FOUNDRY, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public DiamondElectricFoundry() {
    super();
  }

}
