package tech.brettsaunders.craftory.tech.power;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.utils.BlockUtils;

public class PowerManager implements Listener {
  private HashMap<Location, PowerConnector> beamConnectors = new HashMap<>();
  private Craftory plugin;
  private final BlockUtils blockUtils = new BlockUtils();
  private HashMap<Player,Location> usingTool = new HashMap<>();

  public PowerManager() {
    this.plugin = Craftory.getInstance();
  }

  public void onEnable() {
    beamConnectors.forEach(((location, powerConnector) -> powerConnector.buildBeams()));
  }

  public void onDisable() {
    beamConnectors.forEach(((location, powerConnector) -> powerConnector.destroyBeams()));
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Location location = event.getBlockPlaced().getLocation();
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
        () -> {
          if (blockUtils.isCustomBlockType(event.getBlockPlaced(), "craftory:power_connector")) {
            beamConnectors.put(location, new PowerConnector(this,location));
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
            beamConnectors.get(usingTool.get(event.getPlayer())).addConnection(block.getLocation());
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
}
