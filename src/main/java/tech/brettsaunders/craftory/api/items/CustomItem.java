package tech.brettsaunders.craftory.api.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem {

  private int itemID;
  private ItemStack itemStack;
  private String itemName;

  public CustomItem(int itemID, Material itemMaterial, String itemName) {
    this.itemID = itemID;
    this.itemName = itemName;
    generatorItem(itemMaterial);
  }

  public ItemStack getItem() {
    return itemStack.clone();
  }

  private void generatorItem(Material material) {
    itemStack = new ItemStack(material);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setCustomModelData(itemID);
    itemMeta.setDisplayName(itemName);
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemStack.setItemMeta(itemMeta);

    NBTItem nbtItem = new NBTItem(itemStack);
    if (material.isBlock()) {
      nbtItem.setString(CustomItemManager.CUSTOM_BLOCK_ITEM, itemName);
    } else {
      nbtItem.setString(CustomItemManager.CUSTOM_ITEM, itemName);
    }
    nbtItem.setString("NAME", itemName);
    itemStack = nbtItem.getItem();
  }

}
