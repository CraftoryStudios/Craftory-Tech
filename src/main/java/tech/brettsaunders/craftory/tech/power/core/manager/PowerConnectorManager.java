package tech.brettsaunders.craftory.tech.power.core.manager;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.PluginDisableEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.effect.Beam;
import tech.brettsaunders.craftory.utils.BlockUtils;
import tech.brettsaunders.craftory.utils.Blocks;
import tech.brettsaunders.craftory.utils.Items;
import tech.brettsaunders.craftory.utils.Items.Power;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerConnectorManager implements Listener {
  public HashSet<UUID> viewingConnections;

  private HashMap<UUID, Location> formingConnection;
  private transient ArrayList<Beam> activeBeams;

  public PowerConnectorManager() {
    viewingConnections = new HashSet<>();
    formingConnection = new HashMap<>();
    activeBeams = new ArrayList<>();
    generatorPowerBeams();
  }

  @EventHandler
  public void holdingWrench(PlayerItemHeldEvent event) {
    UUID playerUUID = event.getPlayer().getUniqueId();
    if (viewingConnections.contains(playerUUID)) {
      if (!ItemsAdder.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(), Items.Power.WRENCH)) {
        viewingConnections.remove(playerUUID);
      }
    } else if (ItemsAdder.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(), Items.Power.WRENCH)) {
      viewingConnections.add(playerUUID);
    }
  }

  @EventHandler
  public void useWrenchFormConnection(PlayerInteractEvent event) {
    //Check using wrench
    if (ItemsAdder.matchCustomItemName(event.getItem(), Power.WRENCH)
        && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      //Check Power Connector
      if (!BlockUtils.isCustomTypeBlock(event.getClickedBlock(), Blocks.Power.POWER_CONNECTOR)) return;

      if (!formingConnection.containsKey(event.getPlayer().getUniqueId())) {
        //First Power Connector selected
        formingConnection.put(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
        event.getPlayer().sendMessage("Click Second Power Connector To Form Connection");
      } else {
        //Locations
        Location toLoc = event.getClickedBlock().getLocation();
        Location fromLoc = formingConnection.get(event.getPlayer().getUniqueId());

        //Second Power Connector selected
        PowerGridManager powerGridManagerTo = Craftory.getBlockPoweredManager().getPowerGridManager(toLoc);
        PowerGridManager powerGridManagerFrom = Craftory.getBlockPoweredManager().getPowerGridManager(fromLoc);

        //Both have manager and not same power connector
        if (powerGridManagerFrom != null && powerGridManagerTo != null
            && fromLoc != toLoc) {

          //Form Graphical Connection
          formingConnection.remove(event.getPlayer().getUniqueId());
          powerGridManagerFrom.addPowerConnectorConnection(fromLoc, toLoc);

          //Merge Managers
          if (powerGridManagerFrom != powerGridManagerTo) {
            powerGridManagerFrom.combineManagers(powerGridManagerTo);
          }
          formBeam(fromLoc, toLoc);
          event.getPlayer().sendMessage("Connection formed");
        }
      }

    }
  }

  private void generatorPowerBeams() {
    for (PowerGridManager gridManager : Craftory.getBlockPoweredManager().powerGridManagers) {
      gridManager.powerConnectors.forEach((from, value) -> {
        value.forEach((to) -> {
          formBeam(from, to);
        });
      });
    }
  }

  private void formBeam(Location fromLoc, Location toLoc) {
    try {
      Beam beam = new Beam(fromLoc.clone().add(0.5, 0, 0.5), toLoc.clone().add(0.5, 0, 0.5),
          -1, 25);
      beam.start(Craftory.getInstance());
      activeBeams.add(beam);
    } catch (ReflectiveOperationException e) {
      Logger.warn("Couldn't form power beam");
      Logger.debug(e.toString());
    }
  }

  private void destroyActiveBeams() {
    activeBeams.forEach((beam -> {
      beam.stop();
    }));
  }

  @EventHandler
  public void onDisable(PluginDisableEvent event) {
    destroyActiveBeams();
  }

}
