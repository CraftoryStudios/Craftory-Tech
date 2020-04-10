package tech.brettsaunders.extended;

import java.util.HashMap;
import org.bukkit.Location;

public class BeltTree {
  private BeltNode root;
  private HashMap<Location, BeltNode> parents = new HashMap<>();

  public BeltTree(Location location) {
    root = new BeltNode(location);
    parents.put(location, root);
  }

  public BeltNode getRoot() {
    return root;
  }

  public BeltNode getParent(Location location) {
    return parents.get(location);
  }

  public void replaceParent(Location toRemove, Location toAdd, BeltNode node) {
    parents.remove(toRemove);
    parents.put(toAdd, node);
  }

}
