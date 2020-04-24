package tech.brettsaunders.craftory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Barrel implements Listener {

  private String SAVE_PATH = "Barrel.data";

  private HashMap<Location, Inventory> barrels;
  private BlockUtils bs = new BlockUtils();
  public Barrel(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    BarrelData data;
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      data = (BarrelData) in.readObject();
      barrels = data.barrels;
      in.close();
      Bukkit.getLogger().info("*** Barrels Loaded");
    } catch (IOException e) {
      barrels = new HashMap<>();
      Bukkit.getLogger().info("*** New Barrels Created");
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    } catch (Exception e) {
      barrels = new HashMap<>();
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
            if (bs.isCustomBlockType(e.getBlockPlaced(), "craftory:barrel")) {
              //Add the block to the HashSet when it is placed
              barrels.put(e.getBlockPlaced().getLocation(), Bukkit.createInventory(null,54,"Barrel"));
            }
          }
        }, 1L);
  }

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin,
        new Runnable() {
          @Override
          public void run() {
            if (bs.isCustomBlockType(e.getClickedBlock(), "craftory:barrel")) {
              if(barrels.containsKey(e.getClickedBlock().getLocation())){
                Inventory inventory = barrels.get(e.getClickedBlock().getLocation());
                e.getPlayer().openInventory(inventory);
              }
            }
          }
        }, 1L);
  }

  public void save() {
    final BarrelData data = new BarrelData(barrels);
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

    protected HashMap<Location, Inventory>  barrels;

    public BarrelData(HashMap<Location, Inventory>  barrels) {
      this.barrels = barrels;
    }

  }
}
