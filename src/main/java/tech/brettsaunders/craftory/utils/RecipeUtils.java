package tech.brettsaunders.craftory.utils;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import tech.brettsaunders.craftory.CoreHolder;

public class RecipeUtils {

  private static final HashSet<Recipe> allRecipes = new HashSet<>();
  private static final HashSet<Recipe> shapedRecipes = new HashSet<>();
  private static final HashSet<Recipe> shapelessRecipes = new HashSet<>();
  private static final HashSet<Recipe> stonecuttingRecipes = new HashSet<>();
  private static final HashSet<Recipe> furnaceRecipes = new HashSet<>();
  private static final HashSet<Recipe> blastingRecipes = new HashSet<>();
  private static final HashSet<Recipe> smokingRecipeRecipes = new HashSet<>();
  private static final HashSet<Recipe> campfireRecipes = new HashSet<>();
  private static final HashSet<ICustomRecipe> customRecipes = new HashSet<>();
  private static final HashSet<CustomMachineRecipe> twoToOneRecipes = new HashSet<>();

  static {
    Logger.debug("Extracting Bukkit Recipes");
    Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
    while (recipeIterator.hasNext()) {
      Recipe recipe = recipeIterator.next();
      allRecipes.add(recipe);
      if (recipe instanceof ShapedRecipe) { //Ordered most to least common should improve performance
        shapedRecipes.add(recipe);
      } else if (recipe instanceof ShapelessRecipe) {
        shapelessRecipes.add(recipe);
      } else if (recipe instanceof StonecuttingRecipe) {
        stonecuttingRecipes.add(recipe);
      } else if (recipe instanceof FurnaceRecipe) {
        furnaceRecipes.add(recipe);
      } else if (recipe instanceof BlastingRecipe) {
        blastingRecipes.add(recipe);
      } else if (recipe instanceof SmokingRecipe) {
        smokingRecipeRecipes.add(recipe);
      } else if (recipe instanceof CampfireRecipe) {
        campfireRecipes.add(recipe);
      }
    }
    //Add foundry iron + coal -> steel recipe
    HashMap<String, Integer> ingredients = new HashMap<>();
    ingredients.put(Material.COAL.toString(), 1);
    ingredients.put(Material.IRON_INGOT.toString(), 1);
    HashMap<String, Integer> products = new HashMap<>();
    products.put(CoreHolder.Items.STEEL_INGOT, 1);
    twoToOneRecipes.add(new CustomMachineRecipe(ingredients, products));
    Logger.debug("All: " + allRecipes.size());
    Logger.debug("Shaped: " + shapedRecipes.size());
    Logger.debug("Shapeless: " + shapelessRecipes.size());
    Logger.debug("Stone Cutting: " + stonecuttingRecipes.size());
    Logger.debug("Furnace: " + furnaceRecipes.size());
    Logger.debug("Blasting: " + blastingRecipes.size());
    Logger.debug("Smoking: " + smokingRecipeRecipes.size());
    Logger.debug("Campfire: " + campfireRecipes.size());
    Logger.debug("Finished Extracting");
  }

  public static HashSet<Recipe> getAllRecipes() {
    return allRecipes;
  }

  public static HashSet<Recipe> getShapelessRecipes() {
    return shapelessRecipes;
  }

  public static HashSet<Recipe> getShapedRecipes() {
    return shapedRecipes;
  }

  public static HashSet<Recipe> getFurnaceRecipes() {
    return furnaceRecipes;
  }

  public static HashSet<Recipe> getSmokingRecipeRecipes() {
    return smokingRecipeRecipes;
  }

  public static HashSet<Recipe> getBlastingRecipes() {
    return blastingRecipes;
  }

  public static HashSet<Recipe> getCampfireRecipes() {
    return campfireRecipes;
  }

  public static HashSet<Recipe> getStonecuttingRecipes() {
    return stonecuttingRecipes;
  }

  public static HashSet<ICustomRecipe> getCustomRecipes() {
    return customRecipes;
  }

  public static HashSet<CustomMachineRecipe> getTwoToOneRecipes() {
    return twoToOneRecipes;
  }

  public interface ICustomRecipe {

    HashMap<String, Integer> getIngredients();

    HashMap<String, Integer> getProducts();
  }

  public static class CustomMachineRecipe implements ICustomRecipe {

    final HashMap<String, Integer> ingredients;
    final HashMap<String, Integer> products;

    CustomMachineRecipe(HashMap<String, Integer> ingredients, HashMap<String, Integer> products) {
      this.ingredients = ingredients;
      this.products = products;
    }

    @Override
    public HashMap<String, Integer> getIngredients() {
      return ingredients;
    }

    @Override
    public HashMap<String, Integer> getProducts() {
      return products;
    }
  }
}
