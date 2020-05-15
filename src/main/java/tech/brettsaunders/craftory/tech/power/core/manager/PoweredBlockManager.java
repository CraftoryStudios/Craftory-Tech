package tech.brettsaunders.craftory.tech.power.core.manager;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.BlockPowered;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;
import tech.brettsaunders.craftory.tech.power.core.block.BlockCell;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredBlockManager implements Listener {
  private HashMap<Location, BlockPowered> poweredBlocks;
  private static final String DATA_PATH = Craftory.getInstance().getDataFolder().getPath() + File.separator + "PowerManager.data";

  public PoweredBlockManager() {
    poweredBlocks = new HashMap<>();
    init();
  }

  public void onEnable() {
    load();
  }

  public void onDisable() {
    save();
  }

  public void init() {
    Craftory.getInstance().getServer().getPluginManager().registerEvents(this, Craftory.getInstance());
  }

  public void addPoweredBlock(Location location, BlockPowered blockPowered) {
    poweredBlocks.put(location, blockPowered);
  }

  public boolean isPowerReciever(Location location) {
    if (poweredBlocks.get(location) == null) return false;
    return (poweredBlocks.get(location) instanceof IEnergyReceiver);
  }

  public boolean isPowerProvider(Location location) {
    if (poweredBlocks.get(location) == null) return false;
    return (poweredBlocks.get(location) instanceof IEnergyProvider);
  }

  public boolean isPowerStorage(Location location) {
    if (poweredBlocks.get(location) == null) return false;
    return (poweredBlocks.get(location) instanceof IEnergyStorage);
  }

  public BlockPowered getPoweredBlock(Location location) {
    return poweredBlocks.get(location);
  }

  public void removePoweredBlock(Location location) {
    poweredBlocks.remove(location);
  }

  public void load() {
    init();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(DATA_PATH)));
      PowerBlockManagerData data = (PowerBlockManagerData) in.readObject();
      poweredBlocks = data.poweredBlocks;
      in.close();
      Logger.info("PowerBlockManager Loaded");
    } catch (IOException | ClassNotFoundException e) {
      Logger.warn("New PowerBlockManager Data Created");
      Logger.debug(e.toString());
    }
  }

  public void save() {
    try {
      PowerBlockManagerData data = new PowerBlockManagerData(poweredBlocks);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(DATA_PATH)));
      out.writeObject(data);
      out.close();
      Logger.info("PowerBlockManager Data Saved");
    } catch (IOException e) {
      Logger.warn("Couldn't save PowerBlockManager Data");
      Logger.debug(e.toString());
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
    save();
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && poweredBlocks.containsKey(event.getClickedBlock().getLocation())) {
      if (poweredBlocks.get(event.getClickedBlock().getLocation()) instanceof BlockCell) {
        ((BlockCell) poweredBlocks.get(event.getClickedBlock().getLocation())).showInterface(event.getPlayer());
      } else {
        Logger.warn("NOPE");
      }
    }
  }


  private static class PowerBlockManagerData implements Serializable {
    private static transient final long serialVersionUID = -1692723206529286331L;
    protected HashMap<Location, BlockPowered> poweredBlocks;
    public PowerBlockManagerData(HashMap<Location, BlockPowered> poweredBlocks) {
      this.poweredBlocks = poweredBlocks;
    }
  }

}
