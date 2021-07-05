/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.world;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldInitEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.utils.Log;

public class WorldGenHandler implements Listener {

  private OrePopulator orePopulator;
  private final ArrayList<World> registeredWorlds;

  public WorldGenHandler() {
    Events.registerEvents(this);
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
    List<String> blacklist = Craftory.plugin.getConfig().getStringList("ore.blackListedWorlds");
    if(blacklist.contains(e.getWorld().getName())) {
      Log.info("Skipped spawning ores in blacklisted world: " + e.getWorld().getName());
      return;
    }
    if (e.getWorld().getEnvironment() == Environment.NORMAL) {
      e.getWorld().getPopulators().add(orePopulator);
      registeredWorlds.add(e.getWorld());
    }
  }
}
