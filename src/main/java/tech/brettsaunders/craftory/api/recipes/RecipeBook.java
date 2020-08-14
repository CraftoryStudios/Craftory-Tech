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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class RecipeBook implements Listener {
  private static ArrayList<Inventory> inventories = new ArrayList<>();

  public RecipeBook() {
    Events.registerEvents(this);
    String[] keys = Craftory.customRecipeConfig.getConfigurationSection("recipes").getKeys(false).stream().toArray(String[]::new);

    for (int i = 0; i < keys.length; i += 2) {
      ConfigurationSection recipeOne = Craftory.customRecipeConfig.getConfigurationSection("recipes."+keys[i]);
      ConfigurationSection recipeTwo;
      String nameTwo = "";
      if (i+1 >= keys.length) {
        recipeTwo = null;
      } else {
        recipeTwo = Craftory.customRecipeConfig.getConfigurationSection("recipes."+keys[i+1]);
        nameTwo = keys[i+1];
      }
      inventories.add(setupPage(recipeOne, keys[i], recipeTwo, nameTwo));
    }
  }

  public Inventory setupPage(ConfigurationSection recipeOne, String resultOne, ConfigurationSection recipeTwo, String resultTwo) {
    Inventory page = Bukkit.createInventory(null, 54, ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_32.label + NegativeSpaceFont.MINUS_16.label + Font.BOOK.label + NegativeSpaceFont.MINUS_128.label+ NegativeSpaceFont.MINUS_64.label+ NegativeSpaceFont.MINUS_16.label+ ChatColor.DARK_GRAY + resultOne + "                  " + resultTwo);

    setResult(recipeOne, page, 1);
    setGrid(recipeOne, page, 18);

    if (recipeTwo != null) {
      setGrid(recipeTwo, page, 24);
      setResult(recipeTwo, page, 7);
    }

    ItemStack itemStack = CustomItemManager.getCustomItem("titleHider");
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(" ");
    itemStack.setItemMeta(itemMeta);
    page.setItem(45, itemStack);

  return page;
  }

  private void setGrid(ConfigurationSection recipe, Inventory page, int startSlot) {
    HashMap<String,String> ingredients = new HashMap<>();
    for (String key : recipe.getConfigurationSection("ingredients").getKeys(false)) {
      ingredients.put(key,recipe.getString("ingredients."+key));
    }
    List<String> lines = recipe.getStringList("pattern");
    for (int i = 0; i< 3; i++) {
      String[] line = lines.get(i).split("");
      setLine(ingredients, line, page, startSlot + (9*i));
    }

  }

  private void setLine(Map<String, String> ingredients, String[] line, Inventory page, int startSlot) {
    for (int i = 0; i < 3; i++) {
      if (!line[i].equalsIgnoreCase("X")) {
        String itemString = ingredients.get(line[i]);
        ItemStack itemStack = CustomItemManager.getCustomItem(itemString);
        page.setItem(startSlot + i, itemStack);
      }
    }
  }

  private void setResult(ConfigurationSection recipeOne, Inventory page, int slot) {
    ItemStack result = CustomItemManager.getCustomItem(recipeOne.getString("result.item"));
    result.setAmount(recipeOne.getInt("result.amount"));
    page.setItem(slot, result);
  }

  public static Inventory getRecipePage() {
    return inventories.get(0);
  }

}
