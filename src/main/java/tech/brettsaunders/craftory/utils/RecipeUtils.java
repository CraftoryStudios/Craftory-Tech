/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class RecipeUtils {

  @Getter
  private static final ObjectOpenHashSet<Recipe> allRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> shapedRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> shapelessRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> stonecuttingRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final Object2ObjectOpenHashMap<String, String> furnaceRecipes = new Object2ObjectOpenHashMap<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> blastingRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> smokingRecipeRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<Recipe> campfireRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<ICustomRecipe> customRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final ObjectOpenHashSet<CustomMachineRecipe> twoToOneRecipes = new ObjectOpenHashSet<>();
  @Getter
  private static final Object2ObjectOpenHashMap<String, String> maceratorRecipes = new Object2ObjectOpenHashMap<>();
  @Getter
  private static final Object2ObjectOpenHashMap<String, String> magnetiserRecipes = new Object2ObjectOpenHashMap<>();

  static {
    Logger.debug("Extracting Recipes");
    Iterator<Recipe> recipeIterator;
    recipeIterator = Craftory.plugin.getServer().recipeIterator();
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
    /*for(Material material: Material.values()) {
      List<Recipe> recipes =  Craftory.plugin.getServer().getRecipesFor(new ItemStack(material));
      for(Recipe recipe: recipes) {
        if(recipe instanceof FurnaceRecipe) {
          HashMap<String, Integer> ingredients = new HashMap<>();
          ingredients.put(((FurnaceRecipe) recipe).getInput().getType().toString(),((FurnaceRecipe) recipe).getInput().getAmount());
          ArrayList<ItemStack> products = new ArrayList<>();
          products.add(recipe.getResult().clone());
          furnaceRecipes.add(new CustomMachineRecipe(ingredients,products));
        }
      }
    } */
    //Add foundry iron + coal -> steel recipe
    Object2ObjectOpenHashMap<String, Integer> ingredients = new Object2ObjectOpenHashMap<>();
    ingredients.put(Material.CHARCOAL.toString(), 1);
    ingredients.put(Material.IRON_INGOT.toString(), 1);
    ArrayList<ItemStack> products = new ArrayList<>();
    products.add(CustomItemManager.getCustomItem(CoreHolder.Items.STEEL_INGOT));
    twoToOneRecipes.add(new CustomMachineRecipe(ingredients, products));
    ingredients = new Object2ObjectOpenHashMap<>();
    ingredients.put(Items.COAL_DUST, 1);
    ingredients.put(Material.IRON_INGOT.toString(), 1);
    products = new ArrayList<>();
    products.add(CustomItemManager.getCustomItem(CoreHolder.Items.STEEL_INGOT));
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

  public static void addFurnaceRecipe(String source, String result) {
    furnaceRecipes.put(source, result);
  }

  public static void addAllFurnaceRecipes(Object2ObjectOpenHashMap<String, String> recipes) {
    furnaceRecipes.putAll(recipes);
  }

  public static void addAllMaceratorRecipes(Object2ObjectOpenHashMap<String, String> recipes) {
    maceratorRecipes.putAll(recipes);
  }

  public static void addAllMagnetiserRecipes(Object2ObjectOpenHashMap<String, String> recipes) {
    magnetiserRecipes.putAll(recipes);
  }

  public interface ICustomRecipe {

    Object2ObjectOpenHashMap<String, Integer> getIngredients();

    ArrayList<ItemStack> getProducts();
  }

  public static class CustomMachineRecipe implements ICustomRecipe {

    final Object2ObjectOpenHashMap<String, Integer> ingredients;
    final ArrayList<ItemStack> products;

    public CustomMachineRecipe(Object2ObjectOpenHashMap<String, Integer> ingredients,
        ArrayList<ItemStack> products) {
      this.ingredients = ingredients;
      this.products = products;
    }

    @Override
    public Object2ObjectOpenHashMap<String, Integer> getIngredients() {
      return ingredients;
    }

    @Override
    public ArrayList<ItemStack> getProducts() {
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
