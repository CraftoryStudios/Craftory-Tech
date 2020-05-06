package tech.brettsaunders.craftory.tech.power;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.utils.BlockUtils;

public class PowerManager implements Listener {
  private HashMap<Location, PowerConnector> beamConnectors;
  private Craftory plugin;
  private final BlockUtils blockUtils;
  private HashMap<Player,Location> usingTool;

  private final String DATA_PATH;

  public PowerManager() {
    this.plugin = Craftory.getInstance();
    DATA_PATH = plugin.getDataFolder().getPath() + File.separator + "PowerManager.data";
    blockUtils = new BlockUtils();
    usingTool = new HashMap<>();
    beamConnectors = new HashMap<>();
  }

  public void onEnable() {
    load();
    beamConnectors.forEach(((location, powerConnector) -> { powerConnector.onEnable(); powerConnector.buildBeams(); }));
  }

  public void onDisable() {
    save();
    beamConnectors.forEach(((location, powerConnector) -> powerConnector.destroyBeams()));
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
    save();
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Location location = event.getBlockPlaced().getLocation();
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
        () -> {
          if (blockUtils.isCustomBlockType(event.getBlockPlaced(), "craftory:power_connector")) {
            beamConnectors.put(location, new PowerConnector(location));
          }
        }, 1L);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (ItemsAdder.matchCustomItemName(event.getItem(), "itemsadder:ketchup")) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        Block block = event.getClickedBlock();
        if (blockUtils.isCustomBlockType(block, "craftory:power_connector")) {
          if (usingTool.containsKey(event.getPlayer())) {
            Location fromLoc = usingTool.get(event.getPlayer());
            PowerConnector powerConnectorFrom = beamConnectors.get(fromLoc);
            PowerConnector powerConnectorTo = beamConnectors.get(block.getLocation());
            if (powerConnectorTo == null || powerConnectorFrom == null || powerConnectorFrom == powerConnectorTo) {
              event.getPlayer().sendMessage("Error");
              return;
            }
            powerConnectorFrom.addConnection(powerConnectorTo);
            usingTool.remove(event.getPlayer());
          } else {
            usingTool.put(event.getPlayer(), block.getLocation());
            event.getPlayer().sendMessage(ChatColor.RED + "Right click second power connector to form connection!");
          }
        } else {
          if (usingTool.containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage("Not power connector, connection reset!");
            usingTool.remove(event.getPlayer());
          }
        }
      }
    }
  }

  public PowerConnector getPowerConnector(Location location) {
    return beamConnectors.get(location);
  }

  public void load() {
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(DATA_PATH)));
      PowerManagerData data = (PowerManagerData) in.readObject();
      beamConnectors = data.beamConnectors;
      in.close();
      Bukkit.getLogger().info("[Craftory]" + ChatColor.GREEN + "PowerManager Loaded");
    } catch (IOException | ClassNotFoundException e) {
      Bukkit.getLogger().info("[Craftory]" + ChatColor.GREEN + " New PowerManager Data Created");
    }
  }

  public void save() {
    try {
      PowerManagerData data = new PowerManagerData(beamConnectors);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(DATA_PATH)));
      out.writeObject(data);
      out.close();
      Bukkit.getLogger().info("[Craftory]" + ChatColor.GREEN + " PowerManager Data Saved");
    } catch (IOException e) {
      Bukkit.getLogger().info("[Craftory]" + ChatColor.YELLOW + " PowerManager Data failed to save");
    }
  }


  private static class PowerManagerData implements Serializable {
    private static transient final long serialVersionUID = -1692223206529286331L;
    protected HashMap<Location, PowerConnector> beamConnectors;
    public PowerManagerData(HashMap<Location, PowerConnector>beamConnectors) {
      this.beamConnectors = beamConnectors;
    }
  }

}
