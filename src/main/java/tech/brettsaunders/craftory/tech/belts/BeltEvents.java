package tech.brettsaunders.craftory.tech.belts;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tech.brettsaunders.craftory.tech.power.core.block.BlockCell;
import tech.brettsaunders.craftory.utils.BlockUtils;
import tech.brettsaunders.craftory.Craftory;

public class BeltEvents implements Listener {

  BlockUtils blockUtils = new BlockUtils();

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Craftory.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Craftory.getInstance(),
        () -> {

          if (blockUtils.isCustomBlockType(event.getBlockPlaced(), "craftory:belt")) {
            Chunk chunk = event.getBlockPlaced().getWorld().getChunkAt(event.getBlockPlaced());
            Craftory.chunkKeys.add((((long) chunk.getChunkSnapshot().getX()) << 32) | (
                chunk.getChunkSnapshot().getZ() & 0xFFFFFFFFL));

            event.getBlockPlaced().getLocation().add(0, 1, 0).getBlock().breakNaturally();
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
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltsouthsn"));
              } else if (blockSouth) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltsouths"));
              } else if (blockNorth) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltsouthn"));
              } else {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltsouth"));
              }
              blockUtils.onSouthBeltPlace(event.getBlockPlaced());
            } else if (yaw < 135) {
              //WEST
              if (blockEast && blockWest) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltwestew"));
              } else if (blockEast) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltweste"));
              } else if (blockWest) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltwestw"));
              } else {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltwest"));
              }
              blockUtils.onWestBeltPlace(event.getBlockPlaced());
            } else if (yaw < 225) {
              //NORTH
              if (blockSouth && blockNorth) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltns"));
              } else if (blockSouth) {
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extra:belts"));
              } else if (blockNorth) {
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("extra:beltn"));
              } else {
                ItemsAdder.placeCustomBlock(location, ItemsAdder.getCustomItem("craftory:belt"));
              }
              blockUtils.onNorthBeltPlace(event.getBlockPlaced());
            } else if (yaw < 315) {
              //EAST
              if (blockEast && blockWest) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:belteastew"));
              } else if (blockEast) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:belteastee"));
              } else if (blockWest) {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:belteastw"));
              } else {
                ItemsAdder
                    .placeCustomBlock(location, ItemsAdder.getCustomItem("extra:belteast"));
              }
              blockUtils.onEastBeltPlace(event.getBlockPlaced());
            }
            //NORTH
          } else if (blockUtils.isCustomBlockType(event.getBlockPlaced(), "craftory:power_cell")) {
            new BlockCell(event.getBlockPlaced().getLocation());
          }

          Block checkBlock = event.getBlockPlaced().getLocation().add(0, -1, 0).getBlock();
          if (ItemsAdder.isCustomBlock(checkBlock)) {

            //if (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(checkBlock)).equals("craftory:belt"))
            event.getBlockPlaced().breakNaturally();
          }
        }, 1L);
  }
}
