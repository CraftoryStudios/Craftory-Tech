package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class CursedEarth implements Listener, Runnable {

  static private float SPREAD_RATE = 1.0f;
  BlockUtils bs = new BlockUtils();

  public void setEarths(HashSet<Block> earths) {
    this.earths = earths;
  }

  public void setClosedList(HashSet<Block> closedList) {
    this.closedList = closedList;
  }

  BlockFace[] faces = {BlockFace.SELF, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
      BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST,
      BlockFace.NORTH_WEST};

  public HashSet<Block> getEarths() {
    return earths;
  }

  public HashSet<Block> getClosedList() {
    return closedList;
  }

  private HashSet<Block> earths = new HashSet<>();
  private HashSet<Block> closedList = new HashSet<>();

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin,
        new Runnable() {
          @Override
          public void run() {
            if (bs.isCustomBlockType(event.getBlockPlaced(), "craftory:cursed_earth")) {
              earths.add(event.getBlockPlaced()); //Add the block to the HashSet when it is placed
            }
          }
        }, 1L);
  }

  @Override
  public void run() {
    Bukkit.getLogger().info("Earths size: " + earths.size());
    Random random = new Random();
    HashSet<Block> toAdd = new HashSet<>();
    HashSet<Block> toRemove = new HashSet<>();
    for (Block block : earths) {
      if (random.nextInt(4) >= 1) {
        continue;
      } //Stops every block from spreading at the same time, could change this to select random elements rather than iterating and skipping
      ArrayList<Block> valid = generateValidFaces(block); //Get blocks that it can spread to
      if (valid.size() > 0) {
        if (random.nextInt(1000) / SPREAD_RATE <= (3 * valid.size())) {
          Block neighbour = valid.get(random.nextInt(valid.size())); //Picks a random face
          ItemsAdder.placeCustomBlock(neighbour.getLocation(),
              ItemsAdder.getCustomItem("craftory:cursed_earth"));
          toAdd.add(neighbour);
          Bukkit.getLogger().info("Cursed Spread");
          break;
        }
      } else {
        Bukkit.getLogger().info("No valid");
        closedList.add(block);
        toRemove.add(block);
      }
    }
    earths.addAll(toAdd);
    earths.removeAll(toRemove);
  }

  private ArrayList<Block> generateValidFaces(Block block) {
    ArrayList<Block> valid = new ArrayList<>();
    Block blockup = block.getRelative(0, 1, 0);
    Block blockdown = block.getRelative(0, -1, 0);
    Block neighbour;
    for (BlockFace face : faces) {
      neighbour = block.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour);
      }
      neighbour = blockup.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour);
      }
      neighbour = blockdown.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour);
      }
    }
    return valid;
  }
}
