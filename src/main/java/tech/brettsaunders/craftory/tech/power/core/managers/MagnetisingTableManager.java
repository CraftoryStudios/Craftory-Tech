/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.tech.power.core.managers;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser.MagnetisingTable;

public class MagnetisingTableManager implements Listener {


  private HashMap<Location, MagnetisingTable> tables;
  //TODO create
  /*
  @EventHandler
  public void itemFrameHit(EntityDamageByEntityEvent event) {
    if (!(event.getDamager().getType().equals(EntityType.PLAYER))) {
      return;
    }
    ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
    if (itemStack.getType() == Material.AIR) {
      return;
    }
    if (!CustomItemManager.getCustomItemName(itemStack).equals(Items.ENGINEERS_HAMMER)) {
      return;
    }
    if (!event.getEntityType().equals(EntityType.ITEM_FRAME)) {
      return;
    }
    if (!event.getEntity().getLocation().equals(frameLocation)) {
      return;
    }
    boolean hit = frameHit(((Player) event.getDamager()));
    if (hit) {
      event.setCancelled(true);
      ((Player) event.getDamager()).getInventory().setItemInMainHand(
          ToolManager.decreaseDurability(itemStack, 1));
      ;
    }
  }

  @EventHandler
  public void frameBreak(HangingBreakEvent event) {
    if (!framePlaced) {
      return;
    }
    if (event.getEntity().getLocation().equals(frameLocation)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void blockRightClicked(PlayerInteractEvent event) {
    if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (event.getPlayer().isSneaking()) {
      return;
    }
    if (event.getClickedBlock() == null) {
      return;
    }
    if (event.getItem() == null) {
      return;
    }
    if (!event.getClickedBlock().getLocation().equals(location)) {
      return;
    }
    event.setCancelled(true);
    if (!framePlaced) {
      spawnFrame();
    }
    if (itemFrame != null) {
      if (!(itemFrame.getItem().getType().equals(Material.AIR))) {
        return;
      }
      ItemStack item = event.getItem().clone();
      event.getItem().setAmount(item.getAmount() - 1);
      item.setAmount(1);
      itemFrame.setItem(item);
    }

  }

   */

}
