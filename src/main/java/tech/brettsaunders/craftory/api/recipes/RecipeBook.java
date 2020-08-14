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
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.menu.ChestMenu;

public class RecipeBook {

  private static ArrayList<ChestMenu> recipeBookPages = new ArrayList<>();

  public RecipeBook() {
    //Get All Recipes
    String[] keys = Craftory.customRecipeConfig.getConfigurationSection("recipes").getKeys(false).stream().toArray(String[]::new);

    //Loop through recipes two at a time
    for (int i = 0; i < keys.length; i += 2) {
      ConfigurationSection recipeOne = Craftory.customRecipeConfig.getConfigurationSection("recipes."+keys[i]);
      ConfigurationSection recipeTwo;
      //Deal with case of uneven amount of recipes
      String nameTwo = "";
      if (i+1 >= keys.length) {
        recipeTwo = null;
      } else {
        recipeTwo = Craftory.customRecipeConfig.getConfigurationSection("recipes."+keys[i+1]);
        nameTwo = keys[i+1];
      }
      recipeBookPages.add(createPage(i / 2, recipeOne, keys[i], recipeTwo, nameTwo));
    }
  }

  public ChestMenu createPage(int page, ConfigurationSection recipeOne, String resultOne, ConfigurationSection recipeTwo, String resultTwo) {
    //Setup Base Page
    String recipeNames = StringUtils.center(Utilities.getTranslation(resultOne),25) + StringUtils.center(Utilities.getTranslation(resultTwo), 25);
    String title = ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_32.label + NegativeSpaceFont.MINUS_16.label + Font.BOOK.label + NegativeSpaceFont.MINUS_128.label+ NegativeSpaceFont.MINUS_128.label+ ChatColor.DARK_GRAY +" "+ recipeNames;
    ChestMenu chestMenu = new ChestMenu(Craftory.plugin, title);
    chestMenu.setEmptySlotsClickable(false);

    //Setup Title Hider
    ItemStack itemStack = CustomItemManager.getCustomItem("titleHider");
    //Give Blank Name
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(" ");
    itemStack.setItemMeta(itemMeta);
    //Add Item non Clickable
    chestMenu.addItem(46, itemStack, (player, slot, item, cursor,action) -> {return false;});

    //Add Recipe One
    addResult(chestMenu, page, recipeOne, 1);
    addRecipe(chestMenu, page, recipeOne, 18);

    //Add Recipe Two
    if (Objects.nonNull(recipeTwo)) {
      addResult(chestMenu, page, recipeTwo, 7);
      addRecipe(chestMenu, page, recipeTwo, 24);
    }

    //Add Back Button
    if (page != 0) {
      ItemStack backButton = CustomItemManager.getCustomItem("back_button");
      ItemMeta backMeta = backButton.getItemMeta();
      backMeta.setDisplayName(ChatColor.RESET + "Back");
      backButton.setItemMeta(backMeta);
      chestMenu.addItem(45, backButton, (player, slot, item, cursor,action) -> {
        int pageBeforeID = page - 1;
        Optional<ChestMenu> pageBefore = Optional.ofNullable(recipeBookPages.get(pageBeforeID));
        pageBefore.ifPresent((chestMenu1 -> chestMenu1.open(player)));
        return false;
      });
    }

    //Add Forward Button
    ItemStack forwardButton = CustomItemManager.getCustomItem("forward_button");
    ItemMeta forwardMeta = forwardButton.getItemMeta();
    forwardMeta.setDisplayName(ChatColor.RESET + "Forward");
    forwardButton.setItemMeta(forwardMeta);
    chestMenu.addItem(53, forwardButton, (player, slot, item, cursor,action) -> {
      int pageAfterID = page + 1;
      if(pageAfterID < recipeBookPages.size()) {
        Optional<ChestMenu> pageAfter = Optional.ofNullable(recipeBookPages.get(pageAfterID));
        pageAfter.ifPresent((chestMenu1 -> chestMenu1.open(player)));
      }
      return false;
    });

    return chestMenu;
  }

  private void addRecipe(ChestMenu chestMenu, int page, ConfigurationSection recipe, int slot) {
    //Get Ingridents
    HashMap<String,String> ingredients = new HashMap<>();
    for (String key : recipe.getConfigurationSection("ingredients").getKeys(false)) {
      ingredients.put(key,recipe.getString("ingredients."+key));
    }
    //Get Each Line of Recipe
    List<String> lines = recipe.getStringList("pattern");
    for (int i = 0; i< 3; i++) {
      String[] line = lines.get(i).split("");
      addRecipeLine(ingredients, line, chestMenu, slot + (9*i));
    }
  }

  private void addRecipeLine(HashMap<String,String> ingredients, String[] line, ChestMenu chestMenu, int slot) {
    //Add Each item in line of Recipe
    for (int i = 0; i < 3; i++) {
      if (!line[i].equalsIgnoreCase("X")) {
        String itemString = ingredients.get(line[i]);
        //Custom or Minecraft Items
        ItemStack itemStack = CustomItemManager.getCustomItemOrDefault(itemString);
        chestMenu.addItem(slot + i, itemStack, (player, selectedSlot, item, cursor,action) -> {return false;});
      }
    }
  }


  private void addResult(ChestMenu chestMenu, int page, ConfigurationSection recipeOne, int slot) {
    ItemStack result = CustomItemManager.getCustomItem(recipeOne.getString("result.item"));
    //Set Amount if produces multiple
    result.setAmount(recipeOne.getInt("result.amount"));
    //Add Recipe Result to Recipe Book
    chestMenu.addItem(slot, result, (player, i, item, cursor,action) -> {return false;});
  }

  public static void openRecipeBook(Player... players) {
    Optional<ChestMenu> recipeBook = Optional.ofNullable(recipeBookPages.get(0));
    recipeBook.ifPresent((book) -> book.open(players));
  }

}
