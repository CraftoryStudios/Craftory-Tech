package tech.brettsaunders.craftory.tech.power;

import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.Location;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.utils.BlockUtils;

public class PowerConnector implements Serializable {
  private Location location;
  private ArrayList<Location> connectionTo;
  private ArrayList<Location> connectionFrom;
  private PowerManager powerManager;
  private final int maxConnections = 5;
  private ArrayList<Beam> beams = new ArrayList<>();
  private Craftory plugin;

  public PowerConnector(PowerManager powerManager, Location location) {
    this.location = location;
    connectionTo = new ArrayList<>(maxConnections);
    connectionFrom = new ArrayList<>();
    this.powerManager = powerManager;
    this.plugin = Craftory.getInstance();
  }

  public boolean addConnection(Location connectTo) {
    if (connectionTo.size() < maxConnections) {
      connectionTo.add(connectTo);
      //Add Connected from
      powerManager.getPowerConnector(connectTo).addConnectionFrom(location);
      try {
        Beam beam = new Beam(location.add(0.5,0,0.5), connectTo.add(0.5,0,0.5), -1, 25);
        beam.start(plugin);
        beams.add(beam);
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public void addConnectionFrom(Location connectFrom) {
    connectionFrom.add(connectFrom);
  }

  public void buildBeams() {
    connectionTo.forEach((connector -> {
      try {
        Beam beam = new Beam(location, connector, -1, 25);
        beam.start(plugin);
        beams.add(beam);
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    }));
  }

  public void destroyBeams() {
    beams.forEach((beam -> {
      beam.stop();
    }));
  }

}
