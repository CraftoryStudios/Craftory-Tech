/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.tools;

import de.tr7zw.changeme.nbtapi.NBTItem;
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
import tech.brettsaunders.craftory.Constants.Items;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class ToolManager implements Listener {

  private static final HashSet<Material> plants = new HashSet<>();

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

  public static ItemStack decreaseDurability(ItemStack itemStack, int amount) {
    NBTItem nbtItem = new NBTItem(itemStack);
    if (Boolean.TRUE.equals(nbtItem.hasKey("custom_max_durability"))) {
      int currentDurability = nbtItem.getInteger("custom_durability") - amount;
      if (currentDurability > 0) {
        nbtItem.setInteger("custom_durability", currentDurability);
        int maxCustom = nbtItem.getInteger("custom_max_durability");
        itemStack = nbtItem.getItem();
        ItemMeta meta = itemStack.getItemMeta();
        float damage =
            (float) itemStack.getType().getMaxDurability() - (float)currentDurability / (float)maxCustom * (float)itemStack.getType().getMaxDurability();
        ((Damageable) meta).setDamage(Math.round(damage));
        itemStack.setItemMeta(meta);
        itemStack = CustomItemManager.updateDurabilityLore(itemStack,currentDurability, maxCustom);
      } else {
        itemStack.setAmount(0);
      }
    }
    return itemStack;
  }

  @EventHandler
  public void onSickleUse(BlockBreakEvent event) {
    ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
    if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_WOOD)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(),
          2));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_STONE)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 4));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_IRON)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 6));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_GOLD)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 12));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_STEEL)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 8));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_COPPER)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 6));
    } else if (CustomItemManager.matchCustomItemName(itemStack,
        Items.SICKLE_DIAMOND)) {
      itemStack = decreaseDurability(itemStack, getPlantsInRange(event.getBlock().getLocation(), 10));
    } else {
      return;
    }
    event.getPlayer().getInventory().setItemInMainHand(itemStack);
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
