package tech.brettsaunders.craftory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Barrel implements Listener {

  private final Integer BARREL_SIZE = 27;
  private final Integer REINFORCED_BARREL_SIZE = 54;
  private HashMap<Location, Inventory> barrels;
  private final BlockUtils bs = new BlockUtils();
  private String SAVE_PATH = "Barrel.data";

  public Barrel(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    BarrelData data;
    barrels = new HashMap<>();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      data = (BarrelData) in.readObject();
      HashMap<Location, Integer> sizes = data.sizes;
      for (Entry<Location, ItemStack[]> e : data.items.entrySet()) {
        Inventory i = Bukkit.createInventory(null, sizes.get(e.getKey()), "Barrel");
        i.setContents(e.getValue());
        barrels.put(e.getKey(), i);
      }
      in.close();
      Bukkit.getLogger().info("*** Barrels Loaded");
    } catch (IOException e) {
      Bukkit.getLogger().info("*** New Barrels Created");
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    } catch (Exception e) {
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin,
        new Runnable() {
          @Override
          public void run() {
            int size;
            if (bs.isCustomBlockType(e.getBlockPlaced(), "craftory:barrel")) {
              size = BARREL_SIZE;

            } else if (bs.isCustomBlockType(e.getBlockPlaced(), "craftory:reinforced_barrel")) {
              size = REINFORCED_BARREL_SIZE;
            } else {
              return;
            }
            barrels.put(e.getBlockPlaced().getLocation(),
                Bukkit.createInventory(null, size, "Barrel"));
          }
        }, 1L);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Block block = e.getBlock();
    if (bs.isCustomBlockType(block, "craftory:barrel") || bs
        .isCustomBlockType(block, "craftory:reinforced_barrel")) {
      Location loc = block.getLocation();

      if (barrels.containsKey(loc)) {
        Inventory i = barrels.remove(loc);
        for (ItemStack item : i.getContents()) {
          if (item != null) {
            loc.getWorld().dropItemNaturally(loc, item);
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Block block = e.getClickedBlock();
    if (bs.isCustomBlockType(block, "craftory:barrel") || bs
        .isCustomBlockType(block, "craftory:reinforced_barrel")) {
      Location loc = block.getLocation();
      if (barrels.containsKey(loc)) {
        Inventory inventory = barrels.get(loc);
        e.getPlayer().openInventory(inventory);
        e.setCancelled(true);
      }
    }
  }

  public void save() {

    HashMap<Location, ItemStack[]> toSave = new HashMap<>();
    HashMap<Location, Integer> sizes = new HashMap<>();
    for (Entry<Location, Inventory> e : barrels.entrySet()) {
      Location loc = e.getKey();
      Inventory i = e.getValue();
      toSave.put(loc, i.getContents());
      sizes.put(loc, i.getSize());
    }
    BarrelData data = new BarrelData(toSave, sizes);
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));
      out.writeObject(data);
      out.close();
      Bukkit.getLogger().info("Barrel Saved");
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().info("Barrel failed to save " + e);
    }
  }

  private static class BarrelData implements Serializable {

    private static transient final long serialVersionUID = -1692222206529286331L;

    protected HashMap<Location, ItemStack[]> items;
    protected HashMap<Location, Integer> sizes;

    public BarrelData(HashMap<Location, ItemStack[]> items, HashMap<Location, Integer> sizes) {
      this.items = items;
      this.sizes = sizes;
    }

  }
}
