package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class CursedEarth implements Listener, Runnable {

  private String SAVE_PATH = "CursedEarth.data";
  private BlockUtils bs = new BlockUtils();
  BlockFace[] faces = {BlockFace.SELF, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
      BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST,
      BlockFace.NORTH_WEST};
  private float SPREAD_RATE = 1.0f;
  private transient HashSet<Location> earths;
  private transient HashSet<Location> closedList;

  public CursedEarth(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    CursedData data;
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      data = (CursedData) in.readObject();
      earths = data.earths;
      closedList = data.closedList;
      in.close();
      Bukkit.getLogger().info("*** Cursed Earth Loaded");
    } catch (IOException e) {
      earths = new HashSet<>();
      closedList = new HashSet<>();
      Bukkit.getLogger().info("*** New Cursed Earth Created");
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    } catch (Exception e) {
      earths = new HashSet<>();
      closedList = new HashSet<>();
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    }
  }

  public void setSpreadRate(float spreadRate) {
    SPREAD_RATE = spreadRate;
    Bukkit.getLogger().info("Spread rate set to: " + spreadRate);
  }

  public void save() {
    final CursedData data = new CursedData(earths, closedList);
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));
      out.writeObject(data);
      out.close();
      Bukkit.getLogger().info("Cursed Earth Saved");
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().info("Cursed Earth failed to save " + e);
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin,
        new Runnable() {
          @Override
          public void run() {
            if (bs.isCustomBlockType(event.getBlockPlaced(), "craftory:cursed_earth")) {
              //Add the block to the HashSet when it is placed
              earths.add(event.getBlockPlaced().getLocation());
            }
          }
        }, 1L);
  }

  @Override
  public void run() {
    Random random = new Random();
    HashSet<Location> toAdd = new HashSet<>();
    HashSet<Location> toRemove = new HashSet<>();
    for (Location loc : earths) {
      if (random.nextInt(5) >= 1) {
        continue;
      } //Stops every block from spreading at the same time, could change this to select random elements rather than iterating and skipping
      ArrayList<Location> valid = generateValidFaces(
          loc.getBlock()); //Get blocks that it can spread to
      if (valid.size() > 0) {
        if (random.nextInt(10000) <= (3 * valid.size() * SPREAD_RATE)) {
          Location neighbour = valid.get(random.nextInt(valid.size())); //Picks a random face
          ItemsAdder.placeCustomBlock(neighbour, ItemsAdder.getCustomItem("craftory:cursed_earth"));
          toAdd.add(neighbour);
        }
      } else {
        closedList.add(loc);
        toRemove.add(loc);
      }
    }
    earths.addAll(toAdd);
    earths.removeAll(toRemove);
  }

  private ArrayList<Location> generateValidFaces(Block block) {
    ArrayList<Location> valid = new ArrayList<>();
    Block blockup = block.getRelative(0, 1, 0);
    Block blockdown = block.getRelative(0, -1, 0);
    Block neighbour;
    for (BlockFace face : faces) {
      neighbour = block.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour.getLocation());
      }
      neighbour = blockup.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour.getLocation());
      }
      neighbour = blockdown.getRelative(face);
      if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder
          .isCustomBlock(neighbour)) {
        valid.add(neighbour.getLocation());
      }
    }
    return valid;
  }

  private static class CursedData implements Serializable {

    private static transient final long serialVersionUID = -1691012206529286331L;

    protected final HashSet<Location> earths;
    protected final HashSet<Location> closedList;

    public CursedData(HashSet<Location> earths, HashSet<Location> closedList) {
      this.earths = earths;
      this.closedList = closedList;
    }

  }
}
