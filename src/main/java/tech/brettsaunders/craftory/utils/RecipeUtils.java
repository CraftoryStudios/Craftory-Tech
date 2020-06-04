package tech.brettsaunders.craftory.utils;


import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

public class RecipeUtils {

  private static final HashSet<Recipe> allRecipes = new HashSet<>();
  private static final HashSet<Recipe> shapedRecipes = new HashSet<>();
  private static final HashSet<Recipe> shapelessRecipes = new HashSet<>();
  private static final HashSet<Recipe> stonecuttingRecipes = new HashSet<>();
  private static final HashSet<Recipe> furnaceRecipes = new HashSet<>();
  private static final HashSet<Recipe> blastingRecipes = new HashSet<>();
  private static final HashSet<Recipe> smokingRecipeRecipes = new HashSet<>();
  private static final HashSet<Recipe> campfireRecipes = new HashSet<>();


  static {
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
    Logger.info("All: " + allRecipes.size());
    Logger.info("Shaped: " + shapedRecipes.size());
    Logger.info("Shapeless: " + shapelessRecipes.size());
    Logger.info("Stone Cutting: " + stonecuttingRecipes.size());
    Logger.info("Furnace: " + furnaceRecipes.size());
    Logger.info("Blasting: " + blastingRecipes.size());
    Logger.info("Smoking: " + smokingRecipeRecipes.size());
    Logger.info("Campfire: " + campfireRecipes.size());
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

}
