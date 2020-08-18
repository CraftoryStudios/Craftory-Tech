/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.tools;

import de.tr7zw.changeme.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class ToolManager implements Listener {

  private static final ObjectOpenHashSet<Material> plants = new ObjectOpenHashSet<>();

  static {
    plants.addAll(Tag.CROPS.getValues());
    plants.addAll(Tag.FLOWERS.getValues());
    plants.add(Material.FERN);
    plants.add(Material.LARGE_FERN);
    plants.add(Material.TALL_GRASS);
    plants.add(Material.GRASS);
  }

  public ToolManager() {
    Events.registerEvents(this);
  }

  public static void decreaseDurability(ItemStack itemStack, int amount) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta instanceof Damageable) {
      NBTItem nbtItem = new NBTItem(itemStack);
      if (nbtItem.hasKey("custom_max_durability")) {
        int currentDurability = nbtItem.getInteger("custom_durability") - amount;
        int maxCustom = nbtItem.getInteger("custom_max_durability");
        int max = itemStack.getType().getMaxDurability();

        int newDurability = calculateNewDurability(currentDurability, maxCustom, max);
        ((Damageable) itemMeta).setDamage(newDurability);
        itemStack.setItemMeta(itemMeta);
      }

    }
  }

  private static int calculateNewDurability(int current, int customMax, int max) {
    double durability = ((double) current / (double) customMax) * (double) max;
    durability = Math.ceil(durability);
    if (durability == 0 && current != 0) {
      durability = 1;
    }
    if (durability == max && current < customMax) {
      durability--;
    }
    return (int) (max - durability);
  }

  @EventHandler
  public void onSickleUse(BlockBreakEvent event) {
    ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
    if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_WOOD)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 2));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_STONE)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 4));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_IRON)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 6));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_GOLD)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 12));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_STEEL)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 8));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_COPPER)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 6));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_DIAMOND)) {
      decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 10));
    }
  }

  private int getPlantsInRange(Location startPoint, int range) {
    int halfRange = range / 2;
    int amount = 0;
    for (int x = -halfRange; x <= halfRange; x++) {
      for (int z = -halfRange; z <= halfRange; z++) {
        Location location = startPoint.clone().add(x, 0, z);
        if (plants.contains(location.getBlock().getType())) {
          amount++;
          location.getBlock().breakNaturally();
        }
      }
    }
    return amount;
  }

}
