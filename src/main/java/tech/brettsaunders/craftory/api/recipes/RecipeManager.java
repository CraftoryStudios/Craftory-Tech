package tech.brettsaunders.craftory.api.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.RecipeUtils.CustomMachineRecipe;

public class RecipeManager implements Listener {

  private HashMap<String, String> customRecipes;
  private HashMap<String, ItemStack> customFurnaceRecipes; //Map of Source to Result

  public RecipeManager() {
    customRecipes = new HashMap<>();
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
    ConfigurationSection recipes = Craftory.customRecipeConfig.getConfigurationSection("recipes");
    if (recipes == null) {
      Logger.warn("Not Recipes found!");
      return;
    }
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
      } catch (Exception e) {
        Logger.info("THIS IS BROKE: " + recipe + "  " + result.getType().toString());
      }

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
        if (CustomItemManager.getCustomItem(sectionIn.getString(ingredient)).getType()
            == Material.AIR) {
          Material material = Material.getMaterial(sectionIn.getString(ingredient));
          shapedRecipe.setIngredient(key, material);
        } else {
          ItemStack itemStack = CustomItemManager.getCustomItem(sectionIn.getString(ingredient));
          shapedRecipe.setIngredient(key, itemStack.getType());
        }
      }
      Bukkit.getServer().addRecipe(shapedRecipe);
      customRecipes.put(recipe, customItemsInSlots);
    }
    //Furnace Recipes
    ConfigurationSection furnaceRecipes = Craftory.customRecipeConfig.getConfigurationSection("furnace_recipes");
    if (furnaceRecipes == null) {
      Logger.warn("No Furnace Recipes found!");
      return;
    }
    customFurnaceRecipes = new HashMap<>();
    for(String recipe: furnaceRecipes.getKeys(false)){
      ItemStack result = CustomItemManager.getCustomItem(furnaceRecipes.getString(recipe+".result.name"));
      result.setAmount(furnaceRecipes.getInt(recipe+".result.amount"));
      customFurnaceRecipes.put(furnaceRecipes.getString(recipe+".input.name"),result);
      HashMap<String, Integer> ingredients = new HashMap<>();
      ingredients.put(furnaceRecipes.getString(recipe+".input.name"),1);
      ArrayList<ItemStack> products = new ArrayList<>();
      String resultName = furnaceRecipes.getString(recipe+".result.name");
      ItemStack stack;
      if(CustomItemManager.isCustomItemName(resultName)){
        stack = CustomItemManager.getCustomItem(resultName);
      } else {
        stack = new ItemStack(Material.valueOf(resultName));
      }
      stack.setAmount(furnaceRecipes.getInt(recipe+".result.amount"));
      products.add(stack);
      RecipeUtils.addFurnaceRecipe(new CustomMachineRecipe(ingredients,products));
    }
  }

  @EventHandler
  public void onRecipeCompleted(PrepareItemCraftEvent e) {
    String resultName;
    if (e.getInventory().getResult() == null
        || e.getInventory().getResult().getType() == Material.AIR) {
      return;
    }
    if (CustomItemManager.isCustomItem(e.getInventory().getResult(), true)) {
      resultName = CustomItemManager.getCustomItemName(e.getInventory().getResult());
    } else {
      resultName = e.getInventory().getResult().getType().name();
    }

    String pattern = customRecipes.get(resultName);
    if (pattern == null) {
      return;
    }

    String recipePattern = "";
    for (ItemStack itemStack : e.getInventory().getMatrix()) {
      if (itemStack == null || itemStack.getType() == Material.AIR) {
        recipePattern = recipePattern + "X";
      } else if (CustomItemManager.isCustomItem(itemStack, true)) {
        recipePattern = recipePattern + "C";
      } else {
        recipePattern = recipePattern + "N";
      }
    }

    if (!recipePattern.equals(pattern)) {
      e.getInventory().setResult(new ItemStack(Material.AIR));
    }

  }

  @EventHandler
  public void FurnaceSmelt(FurnaceSmeltEvent event) {
    if(!CustomItemManager.isCustomItem(event.getSource(),true)) return;
    String source = CustomItemManager.getCustomItemName(event.getSource());
    if(customFurnaceRecipes.containsKey(source))
    event.setResult(customFurnaceRecipes.get(source).clone());
  }

}
