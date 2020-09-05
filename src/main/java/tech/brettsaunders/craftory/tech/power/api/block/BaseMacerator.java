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
  protected Object2ObjectOpenHashMap<String, String> getRecipes() {
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
