/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class BaseMacerator extends BaseOneToOneMachine {

  /* Construction */
  public BaseMacerator(Location location, String blockName, byte level) {
    super(location, blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
  }

  /* Saving, Setup and Loading */
  public BaseMacerator() {
    super();
  }

  @Override
  protected HashMap<String, String> getRecipes() {
    return RecipeUtils.getMaceratorRecipes();
  }

  @Override
  protected void processComplete() {
    inputSlots.get(0).setAmount(inputSlots.get(0).getAmount() - 1);
    boolean isOre = currentRecipe.getX().substring(currentRecipe.getX().length() - 3)
        .equalsIgnoreCase("ore");
    if (outputSlots.get(0) == null || outputSlots.get(0).getType() == Material.AIR) {
      ItemStack stack = currentProduct.clone();
      if (isOre) {
        stack.setAmount(2);
      }
      outputSlots.set(0, stack);
    } else {
      if (isOre) {
        outputSlots.get(0).setAmount(outputSlots.get(0).getAmount() + 2);
      } else {
        outputSlots.get(0).setAmount(outputSlots.get(0).getAmount() + 1);
      }
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots.get(0));
  }

}
