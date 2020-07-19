package tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
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
    Logger.info(recipes.toString());
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
      frameLocation = location.clone().add(0,1,0);
      itemFrame = (ItemFrame) location.getBlock();
    }
  }

  private boolean spawnFrame() {
    frameLocation = location.clone().add(0,1,0);
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
      //TODO make particle appear to show hit success
      Logger.info("hit");
      if(progress!=processTime) {
        itemFrame.setItem(CustomItemManager.getCustomItem(recipes.get(itemName)));
        //TODO process finish particle
        Logger.info("fin");
        progress = 0;
      }
      return true;
    }
    Logger.info(itemName);
    return false;
  }

  @EventHandler
  public void itemFrameHit(EntityDamageByEntityEvent event) {
    Logger.info("frame hit start");
    if(event.getEntityType().equals(EntityType.ITEM_FRAME)) return;
    Logger.info("is frame");
    Logger.info(frameLocation.toString());
    if(!event.getEntity().getLocation().equals(frameLocation)) return;
    boolean cancel = frameHit();
    Logger.info(cancel+"");
    event.setCancelled(cancel);
  }

  @EventHandler
  public void frameBreak(HangingBreakEvent event) {
      
  }

  @EventHandler
  public void blockRightClicked(PlayerInteractEvent event) {
    if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
    if(event.getClickedBlock()==null) return;
    if(event.getItem()==null) return;
    if(!event.getClickedBlock().getLocation().equals(location)) return;
    if(itemFrame!=null) return;
    if(spawnFrame()){
      ItemStack item = event.getItem();
      event.getItem().setAmount(item.getAmount()-1);
      item.setAmount(1);
      itemFrame.setItem(item);
    }
  }

}
