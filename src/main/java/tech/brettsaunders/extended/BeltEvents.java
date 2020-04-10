package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.Set;
import org.bukkit.Chunk;
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
              Chunk chunk = event.getBlockPlaced().getWorld().getChunkAt(event.getBlockPlaced());
              Extended.chunkKeys.add((((long) chunk.getChunkSnapshot().getX()) << 32) | (chunk.getChunkSnapshot().getZ() & 0xFFFFFFFFL));

              event.getBlockPlaced().getLocation().add(0,1,0).getBlock().breakNaturally();
              float yaw = event.getPlayer().getLocation().getYaw();
              if (yaw < 0) {
                yaw += 360;
              }

              Location location = event.getBlockPlaced().getLocation();
              Boolean blockSouth = !event.getBlockPlaced().getRelative(BlockFace.SOUTH).isEmpty()
                  && !ItemsAdder.isCustomBlock(event.getBlockPlaced().getRelative(BlockFace.SOUTH));
              Boolean blockEast = !event.getBlockPlaced().getRelative(BlockFace.EAST).isEmpty()
                  && !ItemsAdder.isCustomBlock(event.getBlockPlaced().getRelative(BlockFace.EAST));
              Boolean blockWest = !event.getBlockPlaced().getRelative(BlockFace.WEST).isEmpty()
                  && !ItemsAdder.isCustomBlock(event.getBlockPlaced().getRelative(BlockFace.WEST));
              Boolean blockNorth = !event.getBlockPlaced().getRelative(BlockFace.NORTH).isEmpty()
                  && !ItemsAdder.isCustomBlock(event.getBlockPlaced().getRelative(BlockFace.NORTH));

              if (yaw >= 315 || yaw < 45) {
                //SOUTH
                if (blockSouth && blockNorth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltsouthsn"));
                } else if (blockSouth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltsouths"));
                } else if (blockNorth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltsouthn"));
                } else {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltsouth"));
                }

              } else if (yaw < 135) {
                //WEST
                if (blockEast && blockWest) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltwestew"));
                } else if (blockEast) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltweste"));
                } else if (blockWest) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltwestw"));
                } else {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltwest"));
                }
                blockUtils.onWestBeltPlace(event.getBlockPlaced());
              } else if (yaw < 225) {
                //NORTH
                if (blockSouth && blockNorth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltns"));
                } else if (blockSouth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belts"));
                } else if (blockNorth) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:beltn"));
                } else {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belt"));
                }
                blockUtils.onNorthBeltPlace(event.getBlockPlaced());
              }else if (yaw < 315) {
                //EAST
                if (blockEast && blockWest) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belteastew"));
                } else if (blockEast) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belteastee"));
                } else if (blockWest) {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belteastw"));
                } else {
                  ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extended:belteast"));
                }
                blockUtils.onEastBeltPlace(event.getBlockPlaced());
              }
              //NORTH
            }

            Block checkBlock = event.getBlockPlaced().getLocation().add(0,-1,0).getBlock();
            if (ItemsAdder.isCustomBlock(checkBlock)) {

              //if (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(checkBlock)).equals("extended:belt"))
              event.getBlockPlaced().breakNaturally();
            }
          }
        }, 1L);
  }
}
