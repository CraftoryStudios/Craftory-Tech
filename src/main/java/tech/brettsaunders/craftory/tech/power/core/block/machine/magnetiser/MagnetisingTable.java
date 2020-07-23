/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class MagnetisingTable extends CustomBlock implements Listener {

  @Persistent
  protected boolean framePlaced;

  protected ItemFrame itemFrame;

  protected Location frameLocation;

  @Persistent
  protected int progress;
  private static final HashMap<String,String> recipes = RecipeUtils.getMagnetiserRecipes();
  private static final int processTime = 10;

  /* Construction */
  public MagnetisingTable(Location location) {
    super(location, Blocks.MAGNETISING_TABLE);
    Craftory.plugin.getServer().getPluginManager().registerEvents(this,Craftory.plugin);
    progress = 0;
    framePlaced = false;
  }

  /* Saving, Setup and Loading */
  public MagnetisingTable() {
    super();
    Craftory.plugin.getServer().getPluginManager().registerEvents(this,Craftory.plugin);
  }


  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    if(framePlaced) {
      frameLocation = location.clone().add(0.5,1.03125,0.5);
      frameLocation.setPitch(-90);
      itemFrame = (ItemFrame) location.getBlock();
    }
  }

  private boolean spawnFrame() {
    frameLocation = location.clone().add(0.5,1.03125,0.5);
    frameLocation.setPitch(-90);
    if(!frameLocation.getBlock().getType().equals(Material.AIR)) return false;
    itemFrame = location.getWorld().spawn(frameLocation,ItemFrame.class);
    itemFrame.setVisible(false);
    framePlaced = true;
    return true;
  }

  private boolean frameHit() {
    if(itemFrame.getItem().getType().equals(Material.AIR)) return true;
    String itemName = CustomItemManager.getCustomItemName(itemFrame.getItem());
    if(recipes.containsKey(itemName)){
      progress +=1;
      frameLocation.getWorld().spawnParticle(Particle.CRIT_MAGIC, frameLocation, 1);
      if(progress==processTime) {
        itemFrame.setItem(CustomItemManager.getCustomItem(recipes.get(itemName)));
        frameLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, frameLocation, 1);
        progress = 0;
      }
      return true;
    }
    Logger.info(itemName);
    return false;
  }

  @EventHandler
  public void itemFrameHit(EntityDamageByEntityEvent event) {
    if(frameLocation==null) return;
    if(!(event.getDamager().getType().equals(EntityType.PLAYER))) return;
    ItemStack itemStack = ((Player)event.getDamager()).getInventory().getItemInMainHand();
    if(itemStack == null || itemStack.getType() == Material.AIR) return;
    if(!CustomItemManager.getCustomItemName(itemStack).equals(Items.ENGINEERS_HAMMER)) return;
    if(!event.getEntityType().equals(EntityType.ITEM_FRAME)) return;
    if(!event.getEntity().getLocation().equals(frameLocation)) return;
    boolean cancel = frameHit();
    Logger.info(cancel+"");
    event.setCancelled(cancel);
  }

  @EventHandler
  public void frameBreak(HangingBreakEvent event) {
    if(!framePlaced) return;
    if (event.getEntity().getLocation().equals(frameLocation)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void blockRightClicked(PlayerInteractEvent event) {
    if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
    if(event.getClickedBlock()==null) return;
    if(event.getItem()==null) return;
    if(!event.getClickedBlock().getLocation().equals(location)) return;
    if(!(itemFrame==null || itemFrame.getItem().getType().equals(Material.AIR))) return;
    if(itemFrame!=null || spawnFrame()){
      ItemStack item = event.getItem().clone();
      event.getItem().setAmount(item.getAmount()-1);
      item.setAmount(1);
      itemFrame.setItem(item);
    }
  }

}
