/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import org.bukkit.Location;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class BaseElectricFurnace extends BaseOneToOneMachine {


  /* Construction */
  public BaseElectricFurnace(Location location, String blockName, byte level) {
    super(location, blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);

  }

  /* Saving, Setup and Loading */
  public BaseElectricFurnace() {
    super();
  }

  @Override
  protected Object2ObjectOpenHashMap<String, String> getRecipes() {
    return new Object2ObjectOpenHashMap(RecipeUtils.getFurnaceRecipes());
  }

}
