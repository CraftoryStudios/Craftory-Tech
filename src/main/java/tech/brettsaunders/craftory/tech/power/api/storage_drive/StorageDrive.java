/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.tech.power.api.storage_drive;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.Log;

public class StorageDrive {

  public static final String DRIVE_ITEM_START = "drive_item_";
  public static final String ID_KEY = "drive_id";
  public static final String CAPACITY_KEY = "capacity";

  public static List<ItemStack> getItemsFromDrive(@NonNull ItemStack drive) {
    List<ItemStack> items = new ArrayList<>();
    NBTItem nbtItem = new NBTItem(drive);
    int capacity;
    if(nbtItem.hasKey(CAPACITY_KEY)) {
      capacity = nbtItem.getInteger(CAPACITY_KEY);
    } else {
      Log.warn("Storage drive had no capacity");
      return items;
    }
    for (int i = 0; i < capacity; i++) {
      if (nbtItem.hasKey(DRIVE_ITEM_START + i)) {
        items.add(nbtItem.getItemStack(DRIVE_ITEM_START + i));
      }
    }
    ItemMeta meta = drive.getItemMeta();
    meta.setLore(Collections.singletonList(ChatColor.BLUE + Utilities.getTranslation("DriveLoaded")));
    drive.setItemMeta(meta);
    return items;
  }

  public static ItemStack saveItemstoDrive(@NonNull ItemStack drive, @NonNull List<ItemStack> items) {
    NBTItem nbtItem = new NBTItem(drive);
    nbtItem.setUUID(ID_KEY, UUID.randomUUID());
    int capacity;
    if(nbtItem.hasKey(CAPACITY_KEY)) {
      capacity = nbtItem.getInteger(CAPACITY_KEY);
    } else {
      Log.warn("Tried to save items to drive with no capacity set");
      return drive;
    }
    ArrayList<String> lore = new ArrayList<>();
    lore.add(ChatColor.BLUE + Utilities.getTranslation("Stored")  + " " + items.size() + "/" + capacity + ":");
    for (int i = 0; i < capacity; i++) {
      if(items.size() > i && items.get(i)!=null) {
        ItemStack item = items.get(i);
        String name = CustomItemManager.getCustomItemName(item);
        name = Utilities.langProperties.getProperty(name, name);
        lore.add(ChatColor.BLUE + "- " + name  + " x" + item.getAmount());
        nbtItem.setItemStack(DRIVE_ITEM_START + i, item);
      }
      else nbtItem.removeKey(DRIVE_ITEM_START + i);
    }
    drive = nbtItem.getItem();
    ItemMeta meta = drive.getItemMeta();
    meta.setLore(lore);
    drive.setItemMeta(meta);
    return drive;
  }
}
