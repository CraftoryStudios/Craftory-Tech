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
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import tech.brettsaunders.craftory.Craftory;

public class RecipeUtils {
  @Getter
  private static final HashMap<String, String> furnaceRecipes = new HashMap<>();
  @Getter
  private static final HashSet<Recipe> blastingRecipes = new HashSet<>();
  @Getter
  private static final HashSet<Recipe> smokingRecipeRecipes = new HashSet<>();
  @Getter
  private static final HashSet<Recipe> campfireRecipes = new HashSet<>();
  @Getter
  private static final HashSet<ICustomRecipe> customRecipes = new HashSet<>();
  @Getter
  private static final HashSet<CustomMachineRecipe> foundryRecipes = new HashSet<>();
  @Getter
  private static final HashMap<String, String> maceratorRecipes = new HashMap<>();
  @Getter
  private static final HashMap<String, String> magnetiserRecipes = new HashMap<>();

  static {
    Log.debug("Extracting Recipes");
    Iterator<Recipe> recipeIterator;
    recipeIterator = Craftory.plugin.getServer().recipeIterator();
    while (recipeIterator.hasNext()) {
      Recipe recipe = recipeIterator.next();
      if (recipe instanceof FurnaceRecipe) {
        furnaceRecipes.put(((FurnaceRecipe) recipe).getInput().getType().toString(),
            recipe.getResult().getType().toString());
      } else if (recipe instanceof BlastingRecipe) {
        blastingRecipes.add(recipe);
      } else if (recipe instanceof SmokingRecipe) {
        smokingRecipeRecipes.add(recipe);
      } else if (recipe instanceof CampfireRecipe) {
        campfireRecipes.add(recipe);
      }
    }
    Log.debug("Furnace: " + furnaceRecipes.size());
    Log.debug("Blasting: " + blastingRecipes.size());
    Log.debug("Smoking: " + smokingRecipeRecipes.size());
    Log.debug("Campfire: " + campfireRecipes.size());
    Log.debug("Finished Extracting");
  }

  public static void addFurnaceRecipe(String source, String result) {
    furnaceRecipes.put(source, result);
  }

  public static void addAllFurnaceRecipes(Map<String, String> recipes) {
    furnaceRecipes.putAll(recipes);
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
