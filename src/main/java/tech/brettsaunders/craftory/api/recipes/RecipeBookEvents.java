/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.api.recipes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class RecipeBookEvents implements Listener {

  private final HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();
  private final HashSet<UUID> playersToNotRestore = new HashSet<>();

  public RecipeBookEvents() { Events.registerEvents(this); }

  public void savePlayerInventory(Player... players) {
    for(Player player: players) {
      playerInventories.put(player.getUniqueId(),player.getInventory().getContents());
      player.getInventory().clear();
    }
  }

  @EventHandler
  public void onRecipeBookOpen(PlayerInteractEvent e) {
    //Pre-Conditions: Right Click and Recipe Book
    if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
    if (e.getItem() == null) return;
    if (e.getItem().getType() != Material.PAPER) return;
    if (!CustomItemManager.matchCustomItemName(e.getItem(), Items.RECIPE_BOOK)) return;

    //Open Recipe Book
    RecipeBook.openRecipeBook(e.getPlayer());
  }

  @EventHandler
  public void onInventoryCloseEvent(InventoryCloseEvent e) {
    restorePlayerInventory((Player) e.getPlayer());
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent e) {
    restorePlayerInventory(e.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    restorePlayerInventory(e.getPlayer());
  }


  @EventHandler
  public void onPlayerDamage(EntityDamageEvent e) {
    if(e.getEntity() instanceof Player){
      restorePlayerInventory((Player) e.getEntity());
      ((Player) e.getEntity()).closeInventory();
    }
  }

  public void onDisable() {
    playerInventories.forEach((id,inventory) -> {
      Craftory.plugin.getServer().getPlayer(id).getInventory().setContents(inventory);
    });
  }

  public void skipPlayer(UUID id) {
    playersToNotRestore.add(id);
  }

  private void restorePlayerInventory(Player player) {
    if(playersToNotRestore.remove(player.getUniqueId())) return;
    if(playerInventories.containsKey(player.getUniqueId())){
      player.getInventory().setContents(playerInventories.get(player.getUniqueId()));
      playerInventories.remove(player.getUniqueId());
    }
  }
}
