package tech.brettsaunders.craftory.tech.power.core.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.api.effect.Beam;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerConnectorManager implements Listener {

  private final transient HashMap<UUID, Location> formingConnection;
  private final transient HashMap<Location, ArrayList<Beam>> activeBeams;

  public PowerConnectorManager() {
    formingConnection = new HashMap<>();
    activeBeams = new HashMap<>();
    generatorPowerBeams();
  }

  @EventHandler
  public void useWrenchFormConnection(PlayerInteractEvent event) {
    //Check using wrench
    if (CustomItemManager.matchCustomItemName(event.getItem(), CoreHolder.Items.WRENCH)
        && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      //Check Power Connector
      if (Craftory.customBlockManager.isCustomBlockOfType(event.getClickedBlock().getLocation(), CoreHolder.Blocks.POWER_CONNECTOR)) {
        if (!formingConnection.containsKey(event.getPlayer().getUniqueId())) {
          //First Power Connector selected
          if (Craftory.getBlockPoweredManager()
              .getPowerGridManager(event.getClickedBlock().getLocation()) == null) {
            return;
          }
          formingConnection
              .put(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
          event.getPlayer().sendMessage("Click Second Power Connector To Form Connection");
        } else {
          //Locations
          Location toLoc = event.getClickedBlock().getLocation();
          Location fromLoc = formingConnection.get(event.getPlayer().getUniqueId());
          //Second Power Connector selected
          PowerGridManager powerGridManagerTo = Craftory.getBlockPoweredManager()
              .getPowerGridManager(toLoc);
          PowerGridManager powerGridManagerFrom = Craftory.getBlockPoweredManager()
              .getPowerGridManager(fromLoc);
          //Both have manager and not same power connector
          if (powerGridManagerFrom != null && powerGridManagerTo != null
              && !fromLoc.equals(toLoc)) {
            //Form Graphical Connection
            formingConnection.remove(event.getPlayer().getUniqueId());
            powerGridManagerFrom.addPowerConnectorConnection(fromLoc, toLoc);
            //Merge Managers
            if (powerGridManagerFrom != powerGridManagerTo) {
              if(powerGridManagerFrom.getGridSize() >= powerGridManagerTo.getGridSize()) {
                powerGridManagerFrom.combineGrid(powerGridManagerTo);
                powerGridManagerFrom.addPowerConnectorConnection(fromLoc, toLoc);
                Craftory.getBlockPoweredManager().mergeGrids(powerGridManagerTo, powerGridManagerFrom);
              } else {
                powerGridManagerTo.combineGrid(powerGridManagerFrom);
                powerGridManagerTo.addPowerConnectorConnection(fromLoc, toLoc);
                Craftory.getBlockPoweredManager().mergeGrids(powerGridManagerFrom, powerGridManagerTo);
              }

            }
            formBeam(fromLoc, toLoc);
            event.getPlayer().sendMessage("Connection formed");
          } else {
            formingConnection.remove(event.getPlayer().getUniqueId());
            Logger.info("Failed to make connection");
            Logger.debug((powerGridManagerFrom == null) + "");
            Logger.debug((powerGridManagerTo == null) + "");
            Logger.debug((fromLoc == toLoc) + "");
          }
        }
      } else if(Craftory.getBlockPoweredManager().isPoweredBlock(event.getClickedBlock().getLocation()) && formingConnection.containsKey(event.getPlayer().getUniqueId())){

        Location toLoc = event.getClickedBlock().getLocation();
        Location fromLoc = formingConnection.get(event.getPlayer().getUniqueId());
        PowerGridManager gridManager = Craftory.getBlockPoweredManager()
            .getPowerGridManager(fromLoc);
        PoweredBlock block = Craftory.getBlockPoweredManager().getPoweredBlock(toLoc);
        if(block instanceof BaseMachine){
          gridManager.addMachine(fromLoc, toLoc);
        } else if(block instanceof BaseGenerator){
          gridManager.addGenerator(fromLoc, toLoc);
        } else if(block instanceof BaseCell){
          gridManager.addPowerCell(fromLoc,toLoc);
        } else {
          event.getPlayer().sendMessage("block didnt match type");
          formingConnection.remove(event.getPlayer().getUniqueId());
          return;
        }
        formBeam(fromLoc, toLoc);
        event.getPlayer().sendMessage("Machine Connected");
        formingConnection.remove(event.getPlayer().getUniqueId());
      }



    }
  }

  private void generatorPowerBeams() {
    for (PowerGridManager gridManager : Craftory.getBlockPoweredManager().powerGridManagers) {
      gridManager.powerConnectors.forEach((from, value) -> value.forEach((to) -> {
        formBeam(from, to);
      }));
    }
  }

  private void formBeam(Location fromLoc, Location toLoc) {
    /*
    THIS SHOULDN'T WORK OR BE NEEDED
     */
    Location l = fromLoc.clone();
    fromLoc = toLoc.clone();
    toLoc = l.clone();
    /*
    THIS SHOULDN'T WORK OR BE NEEDED
     */
    double x = (fromLoc.getX() - toLoc.getX());
    double y = (fromLoc.getY() - toLoc.getY());
    double z = (fromLoc.getZ() - toLoc.getZ());
    double mag = Math.sqrt((x * x) + (y * y) + (z * z));
    double xAngle = Math.acos(x / mag);
    double yAngle = Math.acos(y / mag);
    double zAngle = Math.acos(z / mag);
    mag -= 1; //reduce size by one to fix the issue
    x -= mag * Math.cos(xAngle);
    y -= mag * Math.cos(yAngle);
    z -= mag * Math.cos(zAngle);
    Location to = toLoc.clone();
    to.add(x, y, z);
    try {
      Beam beam = new Beam(fromLoc.clone().add(0.5, 0.1, 0.5), to.clone().add(0.5, 0.1, 0.5),
          -1, 25);
      beam.start(Craftory.getInstance());
      addBeamToList(fromLoc, beam);
      addBeamToList(toLoc, beam);
    } catch (ReflectiveOperationException e) {
      Logger.warn("Couldn't form power beam");
      Logger.captureError(e);
    }
  }

  private void addBeamToList(Location location, Beam beam){
    ArrayList<Beam> temp;
    if(activeBeams.containsKey(location)){
      temp = activeBeams.get(location);
      temp.add(beam);
      activeBeams.put(location,temp);
    } else {
      activeBeams.put(location,new ArrayList<>(Arrays.asList(beam)));
    }
  }

  public void destroyBeams(Location loc) {
    if(activeBeams.containsKey(loc)){
      activeBeams.get(loc).forEach((Beam::stop));
      activeBeams.remove(loc);
    }
  }

  private void destroyActiveBeams() {
    for(ArrayList<Beam> list: activeBeams.values()){
      list.forEach((Beam::stop));
    }
  }

  @EventHandler
  public void onDisable(PluginDisableEvent event) {
    destroyActiveBeams();
  }

}
