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
import java.util.List;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public class StorageDrive {

  public static final String DRIVE_ITEM_START = "drive_item_";

  public static List<ItemStack> getItemsFromDrive(@NonNull ItemStack drive) {
    List<ItemStack> items = new ArrayList<>();
    NBTItem nbtItem = new NBTItem(drive);
    for (int i = 0; i < 7; i++) {
      if (nbtItem.hasKey(DRIVE_ITEM_START + i)) {
        items.add(nbtItem.getItemStack(DRIVE_ITEM_START + i));
      }
    }
    return items;
  }

  public static ItemStack saveItemstoDrive(@NonNull ItemStack drive, @NonNull List<ItemStack> items) {
    NBTItem nbtItem = new NBTItem(drive);
    for (int i = 0; i < Math.min(items.size(),7); i++) {
      if(items.get(i)!=null) nbtItem.setItemStack(DRIVE_ITEM_START + i, items.get(i));
    }
    drive = nbtItem.getItem();
    return drive;
  }
}
