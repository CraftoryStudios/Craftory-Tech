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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.utils.Log;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageDrive {

  public static final String DRIVE_ITEM_TYPE_START = "drive_item_type_";
  public static final String DRIVE_ITEM_AMOUNT_START = "drive_item_amount_";
  public static final String ID_KEY = "drive_id";
  public static final String CAPACITY_KEY = "capacity";
  public static final String TYPES_KEY = "types";
  private static final String STORED = "Stored";

  public static boolean isDrive(ItemStack itemStack) {
    return isDrive(new NBTItem(itemStack));
  }

  public static boolean isDrive(NBTItem nbtItem) {
    return nbtItem.hasKey(CAPACITY_KEY) && nbtItem.hasKey(TYPES_KEY);
  }
  public static Map<String, Integer> getItemsFromDrive(@NonNull ItemStack drive) {
    Map<String, Integer>items = new HashMap<>();
    NBTItem nbtItem = new NBTItem(drive);
    int types;
    if(isDrive(nbtItem)) {
      types = nbtItem.getInteger(TYPES_KEY);
    } else {
      Log.warn("Storage drive had no capacity or types limit set");
      return items;
    }
    for (int i = 0; i < types; i++) {
      if (nbtItem.hasKey(DRIVE_ITEM_TYPE_START + i) && nbtItem.hasKey(DRIVE_ITEM_AMOUNT_START + i)) {
        items.put(nbtItem.getString(DRIVE_ITEM_TYPE_START + i), nbtItem.getInteger(DRIVE_ITEM_AMOUNT_START + i));
      }
    }
    ItemMeta meta = drive.getItemMeta();
    meta.setLore(Collections.singletonList(ChatColor.BLUE + Utilities.getTranslation("DriveLoaded")));
    drive.setItemMeta(meta);
    return items;
  }

  public static ItemStack saveItemsToDrive(@NonNull ItemStack drive, @NonNull Map<String,Integer> items) {
    NBTItem nbtItem = new NBTItem(drive);
    nbtItem.setUUID(ID_KEY, UUID.randomUUID());
    int capacity;
    int types;
    if(isDrive(nbtItem)) {
      capacity = nbtItem.getInteger(CAPACITY_KEY);
      types = nbtItem.getInteger(TYPES_KEY);
    } else {
      Log.warn("Tried to save items to drive with no capacity or types limit set");
      return drive;
    }
    if (items.size() > types) {
      Log.warn("Too many item types to save to drive");
    }
    ArrayList<String> lore = new ArrayList<>();
    lore.add(ChatColor.BLUE + Utilities.getTranslation(STORED)  + " " + items.size() + "/" + types + " types");
    int totalItems = 0;
    int c = 0;
    for (Entry<String, Integer> entry: items.entrySet()) {
      String name = entry.getKey();
      int amount = entry.getValue();
      totalItems += amount;
      if (totalItems > capacity) {
        Log.warn("Tried to save too many total items to drive");
        break;
      }
      nbtItem.setString(DRIVE_ITEM_TYPE_START + c, name);
      nbtItem.setInteger(DRIVE_ITEM_AMOUNT_START + c, amount);
      name = Utilities.langProperties.getProperty(name, name);
      lore.add(ChatColor.BLUE + "- " + name  + " x" + amount);
      c++;
    }
    for (int i = c; i < types; i++) {
      nbtItem.removeKey(DRIVE_ITEM_TYPE_START + i);
      nbtItem.removeKey(DRIVE_ITEM_AMOUNT_START + i);
    }
    lore.add(1, ChatColor.BLUE + Utilities.getTranslation(STORED)  + " " + totalItems + "/" + capacity + " items");
    drive = nbtItem.getItem();
    ItemMeta meta = drive.getItemMeta();
    meta.setLore(lore);
    drive.setItemMeta(meta);
    return drive;
  }

  public static ItemStack updateLoadedLore(ItemStack drive, Map<String,Integer> items, int types, int capacity) {
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.BLUE + Utilities.getTranslation(STORED)  + " " + items.size() + "/" + types + " types");
    lore.add(1, ChatColor.BLUE + Utilities.getTranslation(STORED)  + " " + totalItemsInDrive(items) + "/" + capacity + " items");
    ItemMeta meta = drive.getItemMeta();
    meta.setLore(lore);
    drive.setItemMeta(meta);
    return drive;
  }

  public static int totalItemsInDrive(Map<String,Integer> items) {
    return items.values().stream().reduce(0, Integer::sum);
  }

}
