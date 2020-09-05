/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.items;

import com.google.common.base.Strings;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomItemManager {

  public static final String CUSTOM_ITEM = "CUSTOM_ITEM";
  public static final String CUSTOM_BLOCK_ITEM = "CUSTOM_BLOCK_ITEM";
  private static final Object2ObjectOpenHashMap<String, CustomItem> itemIDCache = new Object2ObjectOpenHashMap<>();
  private static final ArrayList<String> itemNames = new ArrayList<>();


  public static void setup(FileConfiguration customItemConfig,
      FileConfiguration customBlocksConfig, FileConfiguration customModeData) {
    ConfigurationSection items = customItemConfig.getConfigurationSection("items");
    String displayName = "";
    if (items != null) {
      for (String key : items.getKeys(false)) {
        ConfigurationSection itemSection = items.getConfigurationSection(key);
        Material material = Material
            .getMaterial(itemSection.getString("itemModel").toUpperCase());
        if (material == null) {
          Logger.error(
              key + " Material doesn't exist :" + itemSection.getString("itemModel").toUpperCase());
        } else {
          int itemID = customModeData.getInt("items." + key + ".customModelID");
          //Get Display Name
          displayName = Utilities.getTranslation(key);

          CustomItem customItem = new CustomItem(itemID, material, key, displayName);

          if (itemSection.contains("powered_tool") && itemSection.getBoolean("powered_tool")){
            Craftory.poweredToolManager.addPoweredTool(key);
          }

          /* Add extra data */
          if (itemSection.contains("durability")) {
            customItem.setMaxDurability(itemSection.getInt("durability"));
          }

          if (itemSection.contains("attack_speed")) {
            customItem.setAttackSpeed(itemSection.getInt("attack_speed"));
          }

          if (itemSection.contains("attack_damage")) {
            customItem.setAttackDamage(itemSection.getInt("attack_damage"));
          }

          if(itemSection.contains("unbreakable")) {
            customItem.setUnbreakable(itemSection.getBoolean("unbreakable"));
          }

          if(itemSection.contains("max_charge")) {
            customItem.setMaxCharge(itemSection.getInt("max_charge"));
          }
          itemIDCache.put(key, customItem);
          if (!(customItemConfig.contains("items." + key + ".hideItem") && customItemConfig
              .getBoolean("items." + key + ".hideItem"))) {
            itemNames.add(key);
          }
        }
      }
    }

    ConfigurationSection blocks = customBlocksConfig.getConfigurationSection("blocks");
    if (blocks != null) {
      for (String key : blocks.getKeys(false)) {
        ConfigurationSection block = customBlocksConfig.getConfigurationSection("blocks." + key);
        if (block != null) {
          if (!block.contains("itemModel")) {
            continue;
          }
          Material material = Material.getMaterial(block.getString("itemModel").toUpperCase());
          if (material == null) {
            Logger.error(
                key + " Material doesn't exist :" + block.getString("itemModel").toUpperCase());
          } else {
            int itemID = customModeData.getInt("items." + key + ".customModelID");
            //Set Display Name
            String nameKey = key.replace("_WEST", "").replace("_EAST", "").replace("_SOUTH", "");
            displayName = Utilities.getTranslation(nameKey);
            CustomItem customItem = new CustomItem(itemID, material, nameKey, displayName);
            itemIDCache.put(key, customItem);
            if (!(block.contains("hideItem") && block.getBoolean("hideItem"))) {
              itemNames.add(key);
            }
          }
        }
      }
    }
    Logger.debug("Loaded Items");
  }

  public static ArrayList<String> getItemNames() {
    return itemNames;
  }

  public static ItemStack getCustomItem(String itemName) {
    if (itemIDCache.containsKey(itemName)) {
      CustomItem customItem = itemIDCache.get(itemName);
      return customItem.getItem();
    }
    return new ItemStack(Material.AIR);
  }

  public static ItemStack getCustomItemOrDefault(String itemName) {
    if (itemName.startsWith("TAG-")) {
      String tagName = itemName.replace("TAG-","");
      Tag<Material> materialTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName.toLowerCase()), Material.class);
      if (Objects.nonNull(materialTag)) {
        if (materialTag.getValues().iterator().hasNext()) {
          return new ItemStack(materialTag.getValues().iterator().next());
        }
      }
    }
    if (itemIDCache.containsKey(itemName)) {
      CustomItem customItem = itemIDCache.get(itemName);
      return customItem.getItem();
    }
    Optional<Material> material = Optional.ofNullable(Material.getMaterial(itemName));
    if (material.isPresent()) {
      return new ItemStack(material.get());
    }
    return new ItemStack(Material.AIR);
  }

  public static boolean isCustomItemName(String name) {
    return itemIDCache.containsKey(name);
  }

  public static boolean isCustomItem(ItemStack itemStack, boolean includeBlockItems) {
    NBTItem nbtItem = new NBTItem(itemStack);
    if (nbtItem.hasNBTData()) {
      return nbtItem.hasKey(CUSTOM_ITEM) || (nbtItem.hasKey(CUSTOM_BLOCK_ITEM)
          && includeBlockItems);
    }
    return false;
  }

  public static boolean isCustomBlockItem(ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    return nbtItem.hasNBTData() && nbtItem.hasKey(CUSTOM_BLOCK_ITEM);
  }

  public static boolean matchCustomItemName(ItemStack itemStack, String customItemName) {
    if (itemStack == null || itemStack.getType() == Material.AIR) {
      return false;
    }
    NBTItem nbtItem = new NBTItem(itemStack);
    if (isCustomItem(itemStack, true)) {
      return nbtItem.getString("NAME").equals(customItemName);
    }
    return false;
  }

  public static String getCustomItemName(ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    return getCustomItemName(nbtItem);
  }

  public static String getCustomItemName(NBTItem nbtItem) {
    if (nbtItem.hasKey("NAME")) {
      return nbtItem.getString("NAME");
    }
    return nbtItem.getItem().getType().toString();
  }

  public static void updateItemGraphics(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType() == Material.AIR) {
      return;
    }
    if (!isCustomItem(itemStack, true)) {
      return;
    }
    String customItemName = getCustomItemName(itemStack);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemIDCache.containsKey(customItemName)) {
      itemMeta.setCustomModelData(itemIDCache.get(customItemName).getItemID());
      itemStack.setItemMeta(itemMeta);
    }
  }

  public static void updateInventoryItemGraphics(Inventory inventory) {
    for (ItemStack itemStack : inventory.getContents()) {
      updateItemGraphics(itemStack);
    }
  }

  public static ItemStack updateDurabilityLore(ItemStack item, int current, int max) {
    ItemMeta itemMeta = item.getItemMeta();
    List<String> lore;
    if (itemMeta.hasLore()) {
      lore = itemMeta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    int line = -1;
    for (int i = 0; i < lore.size(); i++) {
      if (lore.get(i).contains("Durability")) {
        line = i;
        break;
      }
    }
    if (line == -1) {
      lore.add("");
      lore.add(ChatColor.WHITE+"Durability "+current + " / "+max);
    } else {
      lore.set(line,ChatColor.WHITE+"Durability "+current + " / "+max);
    }

    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    return item;
  }

  public static ItemStack updateLoreProgressBar(ItemStack item, String title, int max, int progress) {
    ItemMeta itemMeta = item.getItemMeta();
    List<String> lore;
    if (itemMeta.hasLore()) {
      lore = itemMeta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    int line = -1;
    for (int i = 0; i < lore.size(); i++) {
      if (lore.get(i).toLowerCase().startsWith(title.toLowerCase())) {
        line = i;
        break;
      }
    }
    if (line == -1) {
      lore.add(ChatColor.YELLOW+title+": "+ChatColor.WHITE+progress/max+"%  "+getProgressBar(progress,
              max, 10, '█', ChatColor.GREEN, ChatColor.RED));
    } else {
      lore.set(line,
          ChatColor.YELLOW+title+": "+ChatColor.WHITE+progress/max+"%  "+getProgressBar(progress,
              max, 10, '█', ChatColor.GREEN, ChatColor.RED));
    }

    itemMeta.setLore(lore);
    item.setItemMeta(itemMeta);
    return item;
  }

  public static String getProgressBar(int current, int max, int totalBars, char symbol,
      ChatColor completedColor,
      ChatColor notCompletedColor) {
    float percent = (float) current / max;
    int progressBars = (int) (totalBars * percent);

    return Strings.repeat("" + completedColor + symbol, progressBars)
        + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
  }

}
