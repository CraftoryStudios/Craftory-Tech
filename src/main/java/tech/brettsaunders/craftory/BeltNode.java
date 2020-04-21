package tech.brettsaunders.craftory;

import java.io.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BeltNode implements Serializable {

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

  public BeltNode getParentBehind() {
    return parentBehind;
  }

  public void setParentBehind(BeltNode parentBehind) {
    this.parentBehind = parentBehind;
  }

  public BeltNode getParentLeft() {
    return parentLeft;
  }

  public void setParentLeft(BeltNode parentLeft) {
    this.parentLeft = parentLeft;
  }

  public BeltNode getParentRight() {
    return parentRight;
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
