package tech.brettsaunders.craftory.api.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemManager implements Listener {

  private static final HashMap<String, Integer> itemIDCache = new HashMap<>();
  private static final String CUSTOM_ITEM = "CUSTOM_ITEM";
  private static final String CUSTOM_BLOCK_ITEM = "CUSTOM_BLOCK_ITEM";

  public static void setup(FileConfiguration customItemConfig,
      FileConfiguration customBlocksConfig) {
    ConfigurationSection items = customItemConfig.getConfigurationSection("items");
    if (items != null) {
      for (String key : items.getKeys(false)) {
        itemIDCache.put(key, customItemConfig.getInt("items." + key));
      }
    }

    ConfigurationSection blocks = customBlocksConfig.getConfigurationSection("blocks");
    if (blocks != null) {
      for (String key : blocks.getKeys(false)) {
        ConfigurationSection block = customBlocksConfig.getConfigurationSection("blocks." + key);
        if (block != null) {
          itemIDCache.put(key, block.getInt("itemID"));
        }
      }
    }
  }

  public static ItemStack getCustomItem(String itemName, boolean isBlockItem) {
    if (itemIDCache.containsKey(itemName)) {
      ItemStack itemStack;
      if (isBlockItem) {
        itemStack = new ItemStack(Material.STONE);
      } else {
        itemStack = new ItemStack(Material.PAPER);
      }
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setCustomModelData(itemIDCache.get(itemName));
      itemMeta.setDisplayName(itemName);
      itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      itemStack.setItemMeta(itemMeta);

      NBTItem nbtItem = new NBTItem(itemStack);
      if (isBlockItem) { //TODO FIX
        nbtItem.setString(CUSTOM_BLOCK_ITEM, itemName);
      } else {
        nbtItem.setString(CUSTOM_ITEM, itemName);
      }
      nbtItem.setString("NAME", itemName);
      return nbtItem.getItem();
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
