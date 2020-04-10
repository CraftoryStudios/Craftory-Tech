package tech.brettsaunders.extended;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;

public class BiMutliHashMap extends HashMap {
  private HashMap<BeltManager, ArrayList<Location>> inverseMap;
  private HashMap<Location, BeltManager> map;

  public BiMutliHashMap() {
    inverseMap = new HashMap<BeltManager, ArrayList<Location>>();
    map = new HashMap<Location, BeltManager>();
  }

  public Object put(Location key, BeltManager value) {
    if (inverseMap.containsKey((BeltManager) value)) {
      inverseMap.get((BeltManager) value).add((Location) key);
    } else {
      ArrayList<Location> temp = new ArrayList<>();
      temp.add((Location) key);
      inverseMap.put((BeltManager) value, temp);
    }
    return map.put(key, value);
  }

  public Object remove (Location key) {
    BeltManager temp = map.remove(key);
    if (inverseMap.containsKey(temp)) {
      inverseMap.get(temp).remove( key);
    }
    return temp;
  }

  public HashMap<Location, BeltManager> getMap() {
    return map;
  }

  public HashMap<BeltManager, ArrayList<Location>> getInverseMap() {
    return inverseMap;
  }
}
