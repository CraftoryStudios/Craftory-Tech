package tech.brettsaunders.extended;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BeltNode {
  private BeltNode child;
  private BeltNode parentBehind;
  private BeltNode parentLeft;
  private BeltNode parentRight;
  private Location location;

  public BeltNode(Location location) {
    this.location = location;
    Bukkit.getLogger().info(toString());
  }

  public void setChild(BeltNode child) {
    this.child = child;
  }

  public void setParentBehind(BeltNode parentBehind) {
    this.parentBehind = parentBehind;
  }

  public void setParentLeft(BeltNode parentLeft) {
    this.parentLeft = parentLeft;
  }

  public void setParentRight(BeltNode parentRight) {
    this.parentRight = parentRight;
  }

  public Location getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return "BeltNode{" +
        "location=" + location +
        '}';
  }

}
