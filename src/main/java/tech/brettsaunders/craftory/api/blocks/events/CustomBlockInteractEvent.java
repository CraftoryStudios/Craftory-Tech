package tech.brettsaunders.craftory.api.blocks.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class CustomBlockInteractEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  private final Action action;
  private final Block blockClicked;
  private final BlockFace blockFace;
  private final ItemStack itemStack;
  private final Player player;
  private boolean isCancelled;

  public CustomBlockInteractEvent(Action action, Block blockClicked, BlockFace blockFace, ItemStack itemStack, Player player) {
    this.action = action;
    this.blockClicked = blockClicked;
    this.blockFace = blockFace;
    this.itemStack = itemStack;
    this.player = player;
    this.isCancelled = false;
  }

  public Action getAction() {
    return action;
  }

  public Block getBlockClicked() {
    return blockClicked;
  }

  public BlockFace getBlockFace() {
    return blockFace;
  }

  public ItemStack getItem() {
    return itemStack;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
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
