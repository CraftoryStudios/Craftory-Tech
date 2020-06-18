package tech.brettsaunders.craftory.api.blocks.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomBlockBreakEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Location location;
  private final String name;
  private boolean isCancelled;

  public CustomBlockBreakEvent(Location location, String name) {
    this.location = location;
    this.name = name;
    this.isCancelled = false;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Location getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }
}
