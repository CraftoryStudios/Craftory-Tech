/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.bukkit.inventory.*;
import tech.brettsaunders.craftory.Craftory;

public class RecipeUtils {
  @Getter
  private static final Map<String, String> furnaceRecipesExtracted = new HashMap<>();
  @Getter
  private static final Set<FurnaceRecipe> furnaceRecipes = new HashSet<>();
  @Getter
  private static final Set<Recipe> blastingRecipes = new HashSet<>();
  @Getter
  private static final Set<Recipe> smokingRecipeRecipes = new HashSet<>();
  @Getter
  private static final Set<Recipe> campfireRecipes = new HashSet<>();
  @Getter
  private static final Set<ICustomRecipe> customRecipes = new HashSet<>();
  @Getter
  private static final Set<CustomMachineRecipe> foundryRecipes = new HashSet<>();
  @Getter
  private static final Map<String, String> maceratorRecipes = new HashMap<>();
  @Getter
  private static final Map<String, String> magnetiserRecipes = new HashMap<>();
  @Getter
  private static final Set<Recipe> craftingRecipes = new HashSet<>();

  static {
    Log.debug("Extracting Recipes");
    Iterator<Recipe> recipeIterator;
    recipeIterator = Craftory.plugin.getServer().recipeIterator();
    while (recipeIterator.hasNext()) {
      Recipe recipe = recipeIterator.next();
      if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
        craftingRecipes.add(recipe);
      } else if (recipe instanceof FurnaceRecipe) {
        furnaceRecipes.add((FurnaceRecipe) recipe);
        String choices = ((FurnaceRecipe) recipe).getInputChoice().toString();
        String result = recipe.getResult().getType().toString();
        for (String choice: choices.substring(24, choices.length()-2).split(", ")) {
          furnaceRecipesExtracted.put(choice, result);
        }
      } else if (recipe instanceof BlastingRecipe) {
        blastingRecipes.add(recipe);
      } else if (recipe instanceof SmokingRecipe) {
        smokingRecipeRecipes.add(recipe);
      } else if (recipe instanceof CampfireRecipe) {
        campfireRecipes.add(recipe);
      }
    }
    Log.debug("Furnace: " + furnaceRecipesExtracted.size());
    Log.debug("Blasting: " + blastingRecipes.size());
    Log.debug("Smoking: " + smokingRecipeRecipes.size());
    Log.debug("Campfire: " + campfireRecipes.size());
    Log.debug("Finished Extracting");
  }

  public static void addFurnaceRecipe(String source, String result) {
    furnaceRecipesExtracted.put(source, result);
  }

  public static void addCraftingRecipe(Recipe recipe) {
    craftingRecipes.add(recipe);
  }

  public static void addAllFurnaceRecipes(Map<String, String> recipes) {
    furnaceRecipesExtracted.putAll(recipes);
  }

  public static void addAllMaceratorRecipes(Map<String, String> recipes) {
    maceratorRecipes.putAll(recipes);
  }

  public static void addAllMagnetiserRecipes(Map<String, String> recipes) {
    magnetiserRecipes.putAll(recipes);
  }

  public static void addAllFoundryRecipes(Set<CustomMachineRecipe> recipes) {
    foundryRecipes.addAll(recipes);
  }

  public interface ICustomRecipe {

    Map<String, Integer> getIngredients();

    List<ItemStack> getProducts();
  }

  public static class CustomMachineRecipe implements ICustomRecipe {

    final Map<String, Integer> ingredients;
    final List<ItemStack> products;

    public CustomMachineRecipe(Map<String, Integer> ingredients,
        List<ItemStack> products) {
      this.ingredients = ingredients;
      this.products = products;
    }

    @Override
    public Map<String, Integer> getIngredients() {
      return ingredients;
    }

    @Override
    public List<ItemStack> getProducts() {
      return products;
    }
  }

  public static class BinaryRecipe {

    @Getter
    final String ingredient;
    @Getter
    final ItemStack product;

    public BinaryRecipe(String ingredient, ItemStack product) {
      this.ingredient = ingredient;
      this.product = product;
    }
  }


}
