/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.macerator;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMacerator;

public class GoldMacerator extends BaseMacerator {

  /* Static Constants Private */
  private static final byte C_LEVEL = 1;

  /* Construction */
  public GoldMacerator(Location location, Player p) {
    super(location, Blocks.GOLD_MACERATOR, C_LEVEL);
  }

  /* Saving, Setup and Loading */
  public GoldMacerator() {
    super();
  }
}
