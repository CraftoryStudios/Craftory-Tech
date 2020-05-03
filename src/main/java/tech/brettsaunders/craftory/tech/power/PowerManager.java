package tech.brettsaunders.craftory.tech.power;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class PowerManager implements Listener {
  private HashMap<Location, Location> beamConnectors = new HashMap<>();
  private HashSet<Beam> beams = new HashSet<>();

  public void onEnable() {

  }

  public void onDisable() {
    beams.forEach((beam -> beam.stop()));
  }
}
