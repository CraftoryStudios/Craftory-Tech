package tech.brettsaunders.craftory.api.blocks.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomBlockPlaceEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Location location;
  private final String name;
  private final Block blockPlaced;
  private boolean isCancelled;

  public CustomBlockPlaceEvent(Location location, String name, Block block) {
    this.location = location;
    this.name = name;
    this.isCancelled = false;
    this.blockPlaced = block;
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

  public String getCustomBlockName() {
    return name;
  }

  public Block getBlockPlaced() {
    return blockPlaced;
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
