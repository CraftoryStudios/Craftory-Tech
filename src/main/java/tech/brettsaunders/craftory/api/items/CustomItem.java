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

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem {

  private int itemID;
  private ItemStack itemStack;
  private String itemName;
  private String displayName;

  public CustomItem(int itemID, Material itemMaterial, String itemName, String displayName) {
    this.itemID = itemID;
    this.itemName = itemName;
    this.displayName = displayName;
    generatorItem(itemMaterial);
  }

  public ItemStack getItem() {
    return itemStack.clone();
  }

  private void generatorItem(Material material) {
    itemStack = new ItemStack(material);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setCustomModelData(itemID);
    itemMeta.setDisplayName(getDisplayNameColour() + displayName);
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

  public void setMaxDurability(int maxDurability) {
    NBTItem nbtItem = new NBTItem(itemStack);
    nbtItem.setInteger("custom_max_durability", maxDurability);
    nbtItem.setInteger("custom_durability", maxDurability);
    itemStack = nbtItem.getItem();
    ItemMeta meta = itemStack.getItemMeta();
    meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemStack.setItemMeta(meta);
  }

  public void setAttackSpeed(int attackSpeed) {
    ItemMeta meta = itemStack.getItemMeta();
    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", attackSpeed, Operation.ADD_NUMBER, EquipmentSlot.HAND);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
    itemStack.setItemMeta(meta);
  }

  public void setAttackDamage(int attackDamage) {
    ItemMeta meta = itemStack.getItemMeta();
    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", attackDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
    itemStack.setItemMeta(meta);
  }

  private ChatColor getDisplayNameColour() {
    String displayNameChecker = displayName.toLowerCase();
    if (displayNameChecker.contains("Iron")) {
      return ChatColor.GRAY;
    } else if (displayNameChecker.contains("Gold")) {
      return ChatColor.GOLD;
    } else if (displayNameChecker.contains("Diamond")) {
      return ChatColor.BLUE;
    } else if (displayNameChecker.contains("Emerald")) {
      return ChatColor.GREEN;
    } else {
      return ChatColor.RESET;
    }
  }

}
