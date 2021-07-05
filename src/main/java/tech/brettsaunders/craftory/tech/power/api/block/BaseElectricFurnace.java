/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

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
  protected HashMap<String, String> getRecipes() {
    return new HashMap<>(RecipeUtils.getFurnaceRecipes());
  }

}
