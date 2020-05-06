package tech.brettsaunders.craftory.tech.power;

import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.Location;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerConnector implements Serializable {

  private Location location;
  private ArrayList<Location> connectionTo;
  private ArrayList<Location> connectionFrom;
  private final int maxConnections = 5;

  private transient ArrayList<Beam> beams;
  private transient Craftory plugin;
  private transient PowerManager powerManager;

  public PowerConnector(Location location) {
    this.location = location;
    connectionTo = new ArrayList<>(maxConnections);
    connectionFrom = new ArrayList<>();
    onEnable();
  }

  public void onEnable() {
    this.powerManager = Craftory.getPowerManager();
    this.plugin = Craftory.getInstance();
    beams = new ArrayList<>();
  }

  public boolean addConnection(PowerConnector connectTo) {
    if (connectionTo.size() < maxConnections) {
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

}
