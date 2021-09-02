/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.electric_furnace;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class IronElectricFurnace extends BaseElectricFurnace {

  /* Static Constants Private */
  private static final byte C_LEVEL = 0;

  /* Construction */
  public IronElectricFurnace(Location location, Player p) {
    super(location, Blocks.IRON_ELECTRIC_FURNACE, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public IronElectricFurnace() {
    super();
  }

}
