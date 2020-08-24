/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.recipes;

import java.util.HashMap;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class RecipeManager implements Listener {

  private final HashMap<String, String> customRecipes;
  private HashMap<String, ItemStack> customFurnaceRecipes; //Map of Source to Result

  public RecipeManager() {
    customRecipes = new HashMap<>();
    Events.registerEvents(this);
    ConfigurationSection recipes = Craftory.customRecipeConfig.getConfigurationSection("recipes");
    if (recipes == null) {
      Logger.warn("No Crafting Recipes found!");
    } else {
      for (String recipe : recipes.getKeys(false)) {
        String customItemsInSlots = "";
        //Result
        ItemStack result = CustomItemManager
            .getCustomItem(recipes.getString(recipe + ".result.item"));
        result.setAmount(recipes.getInt(recipe + ".result.amount"));

        //Add ShapedRecipe
        NamespacedKey namespacedKey = new NamespacedKey(Craftory.plugin, recipe);
        ShapedRecipe shapedRecipe = null;
        try {
          shapedRecipe = new ShapedRecipe(namespacedKey, result);
          ConfigurationSection sectionIn = recipes.getConfigurationSection(recipe + ".ingredients");
          String[] recipeShape = new String[3];
          int i = 0;
          for (String row : recipes.getStringList(recipe + ".pattern")) {
            recipeShape[i] = row;
            String[] rowSlots = row.split("");
            for (String slot : rowSlots) {
              if (slot.equalsIgnoreCase("X")) {
                customItemsInSlots = customItemsInSlots + "X";
              } else if (CustomItemManager.getCustomItem(sectionIn.getString(slot)).getType()
                  != Material.AIR) {
                customItemsInSlots = customItemsInSlots + "C";
              } else {
                customItemsInSlots = customItemsInSlots + "N";
              }
            }
            i++;
          }
          shapedRecipe.shape(recipeShape[0], recipeShape[1], recipeShape[2]);

          for (String ingredient : sectionIn.getKeys(false)) {
            char key = ingredient.charAt(0);
            final String ingridentMaterial = sectionIn.getString(ingredient);

            //Ingredient TAG
            if (ingridentMaterial.toLowerCase().startsWith("tag-")) {
              String tagName = ingridentMaterial.toLowerCase().replace("tag-","");
              Tag<Material> materialTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName.toLowerCase()), Material.class);
              //Tag Found, using
              if (Objects.nonNull(materialTag)) {
                shapedRecipe.setIngredient(key, new MaterialChoice(materialTag));
              //Tag Missing, using AIR
              } else {
                Logger.warn("Recipe used tag: "+ ingridentMaterial+ " which wasn't a recognised Material Tag. Recipe: "+recipe);
                shapedRecipe.setIngredient(key, Material.AIR);
              }

            //Ingredient Vanilla Item
            } else if (CustomItemManager.getCustomItem(ingridentMaterial).getType()
                == Material.AIR) {
              Material material = Material.getMaterial(ingridentMaterial);
              shapedRecipe.setIngredient(key, material);

            //Ingredient Custom Item
            } else {
              String ingredientName = ingridentMaterial;
              ItemStack itemStack = CustomItemManager.getCustomItem(ingredientName);
              shapedRecipe.setIngredient(key, new ExactChoice(itemStack));
            }
          }
          Bukkit.getServer().addRecipe(shapedRecipe);
          customRecipes.put(recipe, customItemsInSlots);
        } catch (Exception e) {
          Logger.error("RECIPE BROKEN: " + recipe + "  " + result.getType().toString());
          Logger.debug(result + "");
          Logger.error(recipes.getString(recipe + ".result.item"));
          Logger.error("Amount: " + recipes.getInt(recipe + ".result.amount"));
          e.printStackTrace();
        }


      }
    }
    HashMap<String, String> toAdd = new HashMap<>();
    //Furnace Recipes
    ConfigurationSection furnaceRecipes = Craftory.customRecipeConfig
        .getConfigurationSection("furnace_recipes");
    if (furnaceRecipes == null) {
      Logger.warn("No Furnace Recipes found!");
    } else {
      customFurnaceRecipes = new HashMap<>();
      for (String recipe : furnaceRecipes.getKeys(false)) {
        ItemStack result = CustomItemManager
            .getCustomItem(furnaceRecipes.getString(recipe + ".result.name"));
        result.setAmount(furnaceRecipes.getInt(recipe + ".result.amount"));
        customFurnaceRecipes.put(furnaceRecipes.getString(recipe + ".input.name"), result);
        toAdd.put(furnaceRecipes.getString(recipe + ".input.name"),
            furnaceRecipes.getString(recipe + ".result.name"));
      }
      RecipeUtils.addAllFurnaceRecipes(toAdd);
    }
    //Macerator Recipes
    ConfigurationSection maceratorRecipes = Craftory.customRecipeConfig
        .getConfigurationSection("macerator_recipes");
    if (maceratorRecipes == null) {
      Logger.warn("No Macerator Recipes found!");
    } else {
      toAdd = new HashMap<>();
      for (String recipe : maceratorRecipes.getKeys(false)) {
        toAdd.put(maceratorRecipes.getString(recipe + ".input.name"),
            maceratorRecipes.getString(recipe + ".result.name"));
      }
      RecipeUtils.addAllMaceratorRecipes(toAdd);
    }

    //Magnetiser Recipes
    ConfigurationSection magnetiserRecipes = Craftory.customRecipeConfig
        .getConfigurationSection("magnetiser_recipes");
    if (magnetiserRecipes == null) {
      Logger.warn("No Magnetiser Recipes found!");
    } else {
      toAdd = new HashMap<>();
      for (String recipe : magnetiserRecipes.getKeys(false)) {
        toAdd.put(magnetiserRecipes.getString(recipe + ".input.name"),
            magnetiserRecipes.getString(recipe + ".result.name"));
      }
      RecipeUtils.addAllMagnetiserRecipes(toAdd);
    }
  }

  @EventHandler
  public void onFurnaceSmelt(FurnaceSmeltEvent event) {
    if (!CustomItemManager.isCustomItem(event.getSource(), true)) {
      return;
    }
    String source = CustomItemManager.getCustomItemName(event.getSource());
    if (customFurnaceRecipes.containsKey(source)) {
      event.setResult(customFurnaceRecipes.get(source).clone());
    }
  }


}
