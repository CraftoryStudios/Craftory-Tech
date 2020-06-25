package tech.brettsaunders.craftory.api.blocks.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class CustomBlockInteractEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  @Getter
  private final Action action;
  @Getter
  private final Block blockClicked;
  @Getter
  private final BlockFace blockFace;
  @Getter
  private final ItemStack itemStack;
  @Getter
  private final Player player;
  private boolean isCancelled;
  @Getter
  private final CustomBlock customBlock;

  public CustomBlockInteractEvent(Action action, Block blockClicked, BlockFace blockFace,
      ItemStack itemStack, Player player, CustomBlock customBlock) {
    this.action = action;
    this.blockClicked = blockClicked;
    this.blockFace = blockFace;
    this.itemStack = itemStack;
    this.player = player;
    this.isCancelled = false;
    this.customBlock = customBlock;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
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
