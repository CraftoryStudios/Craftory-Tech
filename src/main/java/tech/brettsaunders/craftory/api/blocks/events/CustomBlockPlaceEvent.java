/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;


public class CustomBlockPlaceEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  @Getter
  private final Location location;
  @Getter
  private final String name;
  @Getter
  private final Block blockPlaced;
  private boolean isCancelled;
  @Getter
  private final CustomBlock customBlock;

  public CustomBlockPlaceEvent(Location location, String name, Block block,
      CustomBlock customBlock) {
    this.location = location;
    this.name = name;
    this.isCancelled = false;
    this.blockPlaced = block;
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
