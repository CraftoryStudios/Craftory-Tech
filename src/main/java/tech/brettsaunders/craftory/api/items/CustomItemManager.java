package tech.brettsaunders.craftory.api.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomItemManager implements Listener {

  public static final String CUSTOM_ITEM = "CUSTOM_ITEM";
  public static final String CUSTOM_BLOCK_ITEM = "CUSTOM_BLOCK_ITEM";
  private static final HashMap<String, CustomItem> itemIDCache = new HashMap<>();
  private static final ArrayList<String> itemNames = new ArrayList<>();


  public static void setup(FileConfiguration customItemConfig,
      FileConfiguration customBlocksConfig) {
    ConfigurationSection items = customItemConfig.getConfigurationSection("items");
    if (items != null) {
      for (String key : items.getKeys(false)) {
        Material material = Material
            .getMaterial(customItemConfig.getString("items." + key + ".itemModel").toUpperCase());
        if (material == null) {
          Logger.error(key + " Material doesn't exist :" + customItemConfig
              .getString("items." + key + ".itemModel").toUpperCase());
        } else {
          int itemID = customItemConfig.getInt("items." + key + ".itemID");
          CustomItem customItem = new CustomItem(itemID, material, key);
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
          if (!block.contains("itemModel")) continue;
          Material material = Material.getMaterial(block.getString("itemModel").toUpperCase());
          if (material == null) {
            Logger.error(
                key + " Material doesn't exist :" + block.getString("itemModel").toUpperCase());
          } else {
            int itemID = block.getInt("itemID");
            CustomItem customItem = new CustomItem(itemID, material, key);
            itemIDCache.put(key, customItem);
            itemNames.add(key);
          }
        }
      }
    }
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
    if (nbtItem.hasNBTData() && nbtItem.hasKey("NAME")) {
      return nbtItem.getString("NAME");
    }
    return itemStack.getType().toString();
  }
}
