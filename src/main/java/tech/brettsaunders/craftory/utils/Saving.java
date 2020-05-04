package tech.brettsaunders.craftory.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Saving {

  static final String BIGSPLIT = "&&SPLIT&&";
  static final String SMALLSPLIT = "_SPLIT_";

  public static String locationsToString(Collection<Location> collection) {
    StringJoiner joiner = new StringJoiner(BIGSPLIT);
    String world, x, y, z, pitch, yaw;
    for (Location loc: collection) {
      world = loc.getWorld().getName();
      x = String.valueOf(loc.getX());
      y = String.valueOf(loc.getY());
      z = String.valueOf(loc.getZ());
      pitch = String.valueOf(loc.getPitch());
      yaw = String.valueOf(loc.getYaw());
      joiner.add(String.join(SMALLSPLIT,world,x,y,z,pitch,yaw));
    }
    return joiner.toString();
  }

  public static Collection<Location> stringToLocations(String in) {
    List<Location> list = new ArrayList<>();
    String[] locs = in.split(BIGSPLIT);
    String[] parts;
    for (int i = 0; i < locs.length; i++) {
      parts = locs[i].split(SMALLSPLIT);
      Location location = new Location(Bukkit.getWorld(parts[0]),Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),Float.parseFloat(parts[4]),Float.parseFloat(parts[5]));
      list.add(location);
    }
    return list;
  }
}
