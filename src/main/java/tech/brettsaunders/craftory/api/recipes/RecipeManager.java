/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.recipes;

import static tech.brettsaunders.craftory.Craftory.customRecipeConfig;
import static tech.brettsaunders.craftory.Craftory.defaultRecipes;

import io.th0rgal.oraxen.items.OraxenItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.recipes.RecipeUtils;
import tech.brettsaunders.craftory.utils.recipes.RecipeUtils.CustomMachineRecipe;

public class RecipeManager implements Listener {

  private final HashMap<String, String> customRecipes;
  private HashMap<String, ItemStack> customFurnaceRecipes; //Map of Source to Result
  private static final int version = Integer.parseInt(Craftory.plugin.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3].substring(1).split("_")[1]);
  private static List<String> blackListedWorlds;

  public RecipeManager() {
    blackListedWorlds = Craftory.plugin.getConfig().getStringList("crafting.blackListedWorlds");
    customRecipes = new HashMap<>();
    Events.registerEvents(this);

    // Crafting Recipes
    setupCraftingRecipes(customRecipeConfig.getConfigurationSection("recipes"), false);
    setupCraftingRecipes(defaultRecipes.getConfigurationSection("recipes"), true);

    // Furnace Recipes
    setupFurnaceRecipes(customRecipeConfig.getConfigurationSection("furnace_recipes"), false);
    setupFurnaceRecipes(defaultRecipes.getConfigurationSection("furnace_recipes"), true);

    // Macerator Recipes
    setupMaceratorRecipes(customRecipeConfig.getConfigurationSection("macerator_recipes"), false);
    setupMaceratorRecipes(defaultRecipes.getConfigurationSection("macerator_recipes"), true);

    // Magnetiser Recipes
    setupMagnetiserRecipes(customRecipeConfig.getConfigurationSection("magnetiser_recipes"), false);
    setupMagnetiserRecipes(defaultRecipes.getConfigurationSection("magnetiser_recipes"), true);

    //Foundry Recipes
    setupFoundryRecipes(customRecipeConfig.getConfigurationSection("foundry_recipes"), false);
    setupFoundryRecipes(defaultRecipes.getConfigurationSection("foundry_recipes"), true);
  }

  private void setupMagnetiserRecipes(ConfigurationSection magnetiserRecipes, boolean showWarnings) {
    if (magnetiserRecipes == null) {
      if (showWarnings) Log.warn("No Magnetiser Recipes found!");
    } else {
      Map<String, String> toAdd = new HashMap<>();
      for (String recipe : magnetiserRecipes.getKeys(false)) {
        toAdd.put(magnetiserRecipes.getString(recipe + ".input.name"),
            magnetiserRecipes.getString(recipe + ".result.name"));
      }
      RecipeUtils.addAllMagnetiserRecipes(toAdd);
    }
  }

  private void setupMaceratorRecipes(ConfigurationSection maceratorRecipes, boolean showWarnings) {
    if (maceratorRecipes == null) {
      if (showWarnings) Log.warn("No Macerator Recipes found!");
    } else {
      Map<String, String> toAdd = new HashMap<>();
      for (String recipe : maceratorRecipes.getKeys(false)) {
        toAdd.put(maceratorRecipes.getString(recipe + ".input.name"),
            maceratorRecipes.getString(recipe + ".result.name"));
      }
      RecipeUtils.addAllMaceratorRecipes(toAdd);
    }
  }

  private void setupFurnaceRecipes(ConfigurationSection furnaceRecipes, boolean showWarnings) {
    if (furnaceRecipes == null) {
      if (showWarnings) Log.warn("No Furnace Recipes found!");
    } else {
      customFurnaceRecipes = new HashMap<>();
      HashMap<String, String> toAdd = new HashMap<>();
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
  }

  private void setupFoundryRecipes(ConfigurationSection foundryRecipes, boolean showWarnings) {
    if (foundryRecipes == null) {
      if (showWarnings) Log.warn("No Foundry Recipes found!");
    } else {
      HashSet<CustomMachineRecipe> recipesToAdd = new HashSet<>();
      for (String recipe : foundryRecipes.getKeys(false)) {
        HashMap<String, Integer> ingredients = new HashMap<>();
        ingredients.put(
            foundryRecipes.getString(recipe + ".input1.name"),
            foundryRecipes.getInt(recipe + ".input1.amount"));
        ingredients.put(
            foundryRecipes.getString(recipe + ".input2.name"),
            foundryRecipes.getInt(recipe + ".input2.amount"));
        ArrayList<ItemStack> products = new ArrayList<>();
        String productName = foundryRecipes.getString(recipe + ".result.name");
        ItemStack product;
        if(Material.getMaterial(productName)!=null) {
          product = new ItemStack(Material.valueOf(productName));
        } else {
          product = CustomItemManager.getCustomItemOrDefault(productName);
        }
        product.setAmount(foundryRecipes.getInt(recipe + ".result.amount"));
        products.add(product);
        recipesToAdd.add(new CustomMachineRecipe(ingredients, products));
      }
      RecipeUtils.addAllFoundryRecipes(recipesToAdd);
    }
  }

  private void setupCraftingRecipes(ConfigurationSection recipes, boolean showWarnings) {
    if (recipes == null) {
      if (showWarnings) Log.warn("No Crafting Recipes found!");
    } else {
      for (String recipe : recipes.getKeys(false)) {
        //Check item exists in this version
        int v = recipes.getInt(recipe + ".version");
        if(version < v) continue;
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
            if (ingridentMaterial.toLowerCase(Locale.ROOT).startsWith("tag-")) {
              String tagName = ingridentMaterial.toLowerCase(Locale.ROOT).replace("tag-","");
              Tag<Material> materialTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName.toLowerCase(Locale.ROOT)), Material.class);
              //Tag Found, using
              if (Objects.nonNull(materialTag)) {
                shapedRecipe.setIngredient(key, new MaterialChoice(materialTag));
              //Tag Missing, using AIR
              } else {
                Log.warn("Recipe used tag: "+ ingridentMaterial+ " which wasn't a recognised Material Tag. Recipe: "+recipe);
                shapedRecipe.setIngredient(key, Material.AIR);
              }

            //Oraxen Item
            } else if (ingridentMaterial.toLowerCase(Locale.ROOT).startsWith("oraxen-item/")) {
              shapedRecipe.setIngredient(key,
                  new ExactChoice(OraxenItems.getItemById(ingridentMaterial.toLowerCase(Locale.ROOT).replace("oraxen"
                      + "-item/","")).build()));
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
          Log.error("RECIPE BROKEN: " + recipe + "  " + result.getType().toString());
          Log.debug(result + "");
          Log.error(recipes.getString(recipe + ".result.item"));
          Log.error("Amount: " + recipes.getInt(recipe + ".result.amount"));
          e.printStackTrace();
        }


      }
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
      for (ItemStack item : e.getInventory().getMatrix()) {
        if (item != null && CustomItemManager.isCustomItem(item, true)) {
          e.getInventory().setResult(new ItemStack(Material.AIR));
          return;
        }
      }
      return;
    } else if(!e.getView().getPlayer().hasPermission("craftory.craft") || blackListedWorlds.contains(e.getView().getPlayer().getWorld().getName()) ){
      e.getInventory().setResult(new ItemStack(Material.AIR));
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
  public void onFurnaceSmelt(FurnaceSmeltEvent event) {
    if (!CustomItemManager.isCustomItem(event.getSource(), true)) {
      return;
    }
    String source = CustomItemManager.getCustomItemName(event.getSource());
    if (customFurnaceRecipes.containsKey(source)) {
      event.setResult(customFurnaceRecipes.get(source).clone());
    }
  }

  @EventHandler
  public void onSheepDye(PlayerInteractEntityEvent event) {
    if(event.getHand()!= EquipmentSlot.HAND) return;
    if(!(event.getRightClicked() instanceof Sheep)) return;
    ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
    if(!(itemStack.getType().toString().endsWith("DYE"))) return;
    if(CustomItemManager.isCustomItem(itemStack, false)){
      // Ensure that this item shouldn't dye the sheep
      event.setCancelled(true);
    }
  }

}
