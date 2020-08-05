/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.world;

import java.util.ArrayList;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldInitEvent;
import tech.brettsaunders.craftory.Craftory;

public class WorldGenHandler implements Listener {

  private OrePopulator orePopulator;
  private ArrayList<World> registeredWorlds;

  public WorldGenHandler() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
    registeredWorlds = new ArrayList<>();
  }

  @EventHandler
  public void onEnable(PluginEnableEvent e) {
    orePopulator = new OrePopulator();
  }

  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    registeredWorlds.forEach(world -> world.getPopulators().remove(orePopulator));
  }

  @EventHandler
  public void worldinitEvent(WorldInitEvent e) {
    //Register Populators
    if (e.getWorld().getEnvironment() == Environment.NORMAL) {
      e.getWorld().getPopulators().add(orePopulator);
      registeredWorlds.add(e.getWorld());
    }
  }
}
