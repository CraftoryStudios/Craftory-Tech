/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.menu;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;

public final class InvUtils {

  /**
   * This field represents a static and final instance of {@link MenuClickHandler} that prevents clicks.
   */
  public static final MenuClickHandler EMPTY_CLICK = (p, slot, item, cursor, action) -> false;

  private InvUtils() {}

  /**
   * Returns whether the Inventory has at least one empty slot
   *
   * @param inv
   *            The Inventory to check
   * @return Whether an empty slot exists
   */
  public static boolean hasEmptySlot(@NonNull Inventory inv) {
    return inv.firstEmpty() != 1;
  }

  /**
   * This method checks if an Item can fit into the specified slots.
   * Note that this also checks {@link ItemStack#getAmount()}
   *
   * If you do not specify any Slots, all Slots of the Inventory will be checked.
   *
   * @param inv
   *            The inventory to check
   * @param item
   *            The Item that shall be tested for
   * @param slots
   *            The Slots that shall be iterated over
   *
   * @return Whether the slots have space for the {@link ItemStack}
   */
  public static boolean fits(@NonNull Inventory inv, @NonNull ItemStack item, int... slots) {
    if (slots.length == 0) {
      slots = IntStream.range(0, inv.getSize()).toArray();
    }

    for (int slot : slots) {
      ItemStack stack = inv.getItem(slot);

      if (stack == null || stack.getType() == Material.AIR) {
        return true;
      }
      else if (stack.getAmount() + item.getAmount() <= stack.getMaxStackSize() && ItemUtils.canStack(stack, item)) {
        return true;
      }
    }

    return false;
  }

  /**
   * This method works similar to {@link #fits(Inventory, ItemStack, int...)} but allows this check to be done for
   * multiple {@link ItemStack ItemStacks}.
   *
   * If you do not specify any Slots, all Slots of the Inventory will be checked.
   *
   * @param inv
   *            The inventory to check
   * @param items
   *            The Items that shall be tested for
   * @param slots
   *            The Slots that shall be iterated over
   *
   * @return Whether the slots have space for the given {@link ItemStack ItemStacks}
   */
  public static boolean fitAll(@NonNull Inventory inv, @NonNull ItemStack[] items, int... slots) {
    if (slots.length == 0) {
      slots = IntStream.range(0, inv.getSize()).toArray();
    }

    if (items.length == 0) {
      return true;
    }
    else if (items.length == 1) {
      return fits(inv, items[0], slots);
    }

    Object2ObjectOpenHashMap<Integer, ItemStack> cache = new Object2ObjectOpenHashMap<>();

    for (int i = 0; i < items.length; i++) {
      ItemStack item = items[i];
      boolean resolved = false;

      for (int slot : slots) {
        ItemStack stack = cache.getOrDefault(slot, inv.getItem(slot));

        if (stack == null || stack.getType() == Material.AIR) {
          cache.put(slot, items[i]);
          resolved = true;
        }
        else if (stack.getAmount() + item.getAmount() <= stack.getMaxStackSize() && ItemUtils.canStack(stack, item)) {
          ItemStack clone = stack.clone();
          clone.setAmount(stack.getAmount() + item.getAmount());
          cache.put(slot, clone);
          resolved = true;
        }

        if (resolved) {
          break;
        }
      }

      if (!resolved) {
        return false;
      }
    }

    return true;
  }

  /**
   * This method removes a given amount of items from a given {@link Inventory}
   * if they pass the given {@link Predicate}
   *
   * @param inv
   *            The {@link Inventory} from which to remove the item
   * @param amount
   *            The amount of items that should be removed
   * @param replaceConsumables
   *            Whether to replace consumables, e.g. turn potions into glass bottles etc...
   * @param predicate
   *            The Predicate that tests the item
   * @return Whether the operation was successful
   */
  public static boolean removeItem(@NonNull Inventory inv, int amount, boolean replaceConsumables, @NonNull Predicate<ItemStack> predicate) {
    int removed = 0;
    for (int slot = 0; slot < inv.getSize(); slot++) {
      ItemStack item = inv.getItem(slot);

      if (item != null && predicate.test(item)) {
        if (item.getAmount() + removed >= amount) {
          ItemUtils.consumeItem(item, amount - removed, replaceConsumables);
          return true;
        }
        else if (item.getAmount() > 0) {
          removed += item.getAmount();
          ItemUtils.consumeItem(item, item.getAmount(), replaceConsumables);
        }
      }
    }

    return false;
  }

}
