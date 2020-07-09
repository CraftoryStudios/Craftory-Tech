package tech.brettsaunders.craftory.api.recipes;

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
        Logger.error("THIS IS BROKE: " + recipe + "  " + result.getType().toString());
        Logger.error(result + "");
        Logger.error(recipes.getString(recipe + ".result.item"));
        Logger.error("Amount: " + recipes.getInt(recipe + ".result.amount"));
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
    HashMap<String, String> toAdd = new HashMap<>();
    for(String recipe: furnaceRecipes.getKeys(false)){
      ItemStack result = CustomItemManager.getCustomItem(furnaceRecipes.getString(recipe+".result.name"));
      result.setAmount(furnaceRecipes.getInt(recipe+".result.amount"));
      customFurnaceRecipes.put(furnaceRecipes.getString(recipe+".input.name"),result);
      toAdd.put(furnaceRecipes.getString(recipe+".input.name"), furnaceRecipes.getString(recipe+".result.name"));
    }
    RecipeUtils.addAllFurnaceRecipes(toAdd);

    //Macerator Recipes
    ConfigurationSection maceratorRecipes = Craftory.customRecipeConfig.getConfigurationSection("macerator_recipes");
    if (maceratorRecipes == null) {
      Logger.warn("No Macerator Recipes found!");
      return;
    }
    toAdd = new HashMap<>();
    for(String recipe: maceratorRecipes.getKeys(false)){
      toAdd.put(maceratorRecipes.getString(recipe+".input.name"), maceratorRecipes.getString(recipe+".result.name"));
    }
    Logger.info(maceratorRecipes.toString());
    RecipeUtils.addAllMaceratorRecipes(toAdd);
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
      for(ItemStack item: e.getInventory().getMatrix()){
        if(CustomItemManager.isCustomItem(item, true)){
          e.getInventory().setResult(new ItemStack(Material.AIR));
          return;
        }
      }
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
