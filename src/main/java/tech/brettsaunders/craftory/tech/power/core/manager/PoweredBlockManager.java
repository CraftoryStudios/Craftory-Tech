package tech.brettsaunders.craftory.tech.power.core.manager;

import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.core.block.BlockCell;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredBlockManager implements Listener {

  private static final String DATA_PATH = Craftory.getInstance().getDataFolder().getPath() + File.separator + "PowerManager.data";

  private HashMap<Location, PoweredBlock> poweredBlocks;

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

  public void addPoweredBlock(Location location, PoweredBlock blockPowered) {
    poweredBlocks.put(location, blockPowered);
  }

  public PoweredBlock getPoweredBlock(Location location) {
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
  public void onGUIOpen(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (poweredBlocks.containsKey(event.getClickedBlock().getLocation())) {
        //Open GUI of Powered Block
        poweredBlocks.get(event.getClickedBlock().getLocation()).openGUI(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPoweredBlockPlace(BlockPlaceEvent event) {
    Location location = event.getBlockPlaced().getLocation();
    Craftory.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Craftory.getInstance(),
        () -> {
          switch (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(event.getBlockPlaced()))) {
            case "craftory:power_cell":
              addPoweredBlock(location, new BlockCell(location));
              break;
          }
        }, 1L);
  }

  private static class PowerBlockManagerData implements Serializable {
    private static transient final long serialVersionUID = -1692723206529286331L;
    protected HashMap<Location, PoweredBlock> poweredBlocks;
    public PowerBlockManagerData(HashMap<Location, PoweredBlock> poweredBlocks) {
      this.poweredBlocks = poweredBlocks;
    }
  }

}
