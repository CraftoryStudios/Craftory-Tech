package tech.brettsaunders.craftory.tech.power.core.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.effect.Beam;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerManager;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerConnector implements Serializable, Listener {

  private final int MAX_CONNECTIONS = 5;
  private final int MAX_POWER = 100;

  private Location location;
  private ArrayList<Location> connectionTo;
  private ArrayList<Location> connectionFrom;
  private Integer powerLevel;
  private HashMap<BlockFace, Boolean> outputs;

  private transient ArrayList<Beam> beams;
  private transient Craftory plugin;
  private transient PowerManager powerManager;


  public PowerConnector(Location location) {
    this.location = location;
    this.connectionTo = new ArrayList<>(MAX_CONNECTIONS);
    this.connectionFrom = new ArrayList<>();
    this.powerLevel = 0;

    //Power Output Sides
    this.outputs = new HashMap<>();
    outputs.put(BlockFace.DOWN, false);
    outputs.put(BlockFace.UP, false);
    outputs.put(BlockFace.EAST, false);
    outputs.put(BlockFace.WEST, false);
    outputs.put(BlockFace.NORTH, false);
    outputs.put(BlockFace.SOUTH, false);

    onEnable();
  }


  public void onEnable() {
    this.powerManager = Craftory.getPowerManager();
    this.plugin = Craftory.getInstance();
    this.beams = new ArrayList<>();
  }


  public boolean addConnection(PowerConnector connectTo) {
    if (connectionTo.size() < MAX_CONNECTIONS) {
      connectionTo.add(connectTo.getLocation());
      //Add Connected from
      connectTo.addConnectionFrom(location);
      formBeam(connectTo.getLocation());
    }
    return false;
  }


  public void addConnectionFrom(Location connectFrom) {
    connectionFrom.add(connectFrom);
  }


  public void buildBeams() {
    connectionTo.forEach((connector -> {
      formBeam(connector);
    }));
    Logger.debug("All power beams built");
  }


  private void formBeam(Location connector) {
    try {
      Beam beam = new Beam(location.clone().add(0.5, 0, 0.5), connector.clone().add(0.5, 0, 0.5),
          -1, 25);
      beam.start(plugin);
      beams.add(beam);
    } catch (ReflectiveOperationException e) {
      Logger.warn("Couldn't form power beam");
      Logger.debug(e.toString());
    }
  }


  public void destroyBeams() {
    beams.forEach((beam -> {
      beam.stop();
    }));
  }


  public Location getLocation() {
    return location;
  }

  private void toggleOutputSide(BlockFace side) {
    outputs.replace(side, (outputs.get(side) ? false : true));
  }

  private int increasePowerLevel(int amount) {
    powerLevel += amount;
    int powerLeftOver = powerLevel - MAX_POWER;
    return (powerLeftOver < 0) ? 0 : powerLeftOver;
  }

  private boolean isMaxCapacity() {
    return (powerLevel < MAX_POWER) ? false : true;
  }

  public void openGUI(Player player) {
    player.sendMessage("Opened");
    Inventory inventory = Bukkit.createInventory(null, 54, "PowerLevel: " + powerLevel);
    player.openInventory(inventory);
  }

}
