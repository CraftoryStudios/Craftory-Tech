package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BeltEvents implements Listener {
  BlockUtils blockUtils = new BlockUtils();

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Extended.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Extended.plugin,
        new Runnable() {
          @Override
          public void run() {

            if (blockUtils.isCustomBlockType(event.getBlockPlaced(), "extended:belt")) {
              event.getBlockPlaced().getLocation().add(0,1,0).getBlock().breakNaturally();
              float yaw = event.getPlayer().getLocation().getYaw();
              if (yaw < 0) {
                yaw += 360;
              }

              Location location = event.getBlockPlaced().getLocation();

              if (yaw >= 315 || yaw < 45) {
                //SOUTH
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belt"));
              } else if (yaw < 135) {
                //WEST
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belt1"));
              } else if (yaw < 225) {
                //NORTH
              }else if (yaw < 315) {
                //EAST
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belt"));
              }
              //NORTH
            }

            if (ItemsAdder.isCustomBlock(event.getBlockPlaced())) {
              event.getPlayer().sendMessage(ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(event.getBlockPlaced())));
            }

            Block checkBlock = event.getBlockPlaced().getLocation().add(0,-1,0).getBlock();
            if (ItemsAdder.isCustomBlock(checkBlock)) {

              if (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(checkBlock)).equals("extended:belt"))
              event.getBlockPlaced().breakNaturally();
            }
          }
        }, 1L);
  }
}
