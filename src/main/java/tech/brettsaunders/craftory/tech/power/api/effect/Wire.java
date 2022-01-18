/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.effect;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.Constants;
import tech.brettsaunders.craftory.api.items.CustomItemManager;


public class Wire {

  private final int distanceSquared;
  private Location start;
  private Location end;
  private BukkitRunnable run;
  private static final DustOptions particleOptions = new DustOptions(Color.ORANGE, 1F);
  static final float STEP = 0.25f;

  /**
   * Create a Beam instance
   *
   * @param start Location where Beam will starts
   * @param end Location where Beam will ends
   * @param distance Distance where Beam will be visible
   */
  public Wire(Location start, Location end, int distance) {
    this.start = start;
    this.end = end;
    distanceSquared = distance * distance;
  }

  public void start(Plugin plugin) {
    Validate.isTrue(run == null, "Task already started");
    run = new BukkitRunnable() {

      @Override
      public void run() {
          for (Player p : start.getWorld().getPlayers()) {
            if (isCloseEnough(p.getLocation(), p)) {
              spawnLine(start, end);
            }
          }
      }

    };
    run.runTaskTimerAsynchronously(plugin, 0L, 5L);
  }

  public void stop() {
    if (run != null) {
      run.cancel();
    }
  }

  public Location getEnd() {
    return end;
  }

  private void spawnLine(Location start, Location end) {
    Location diff = end.clone().subtract(start);
    Vector move = new Vector(diff.getX(), diff.getY(), diff.getZ());
    double length = move.length();
    move.normalize();
    move.multiply(STEP);
    Location currentPos = start.clone();
    for (float i = 0f; i < length; i+=STEP) {
      spawnParticle(currentPos);
      currentPos.add(move);
    }
  }

  private void spawnParticle(Location l) {
    l.getWorld().spawnParticle(Particle.REDSTONE, l.getX(), l.getY(), l.getZ(), 1, 0, 0, 0, 1, particleOptions);
  }

  private boolean isCloseEnough(Location location, Player player) {
    if (CustomItemManager
        .matchCustomItemName(player.getInventory().getItemInMainHand(), Constants.Items.WRENCH) ||
        CustomItemManager.matchCustomItemName(player.getInventory().getItemInMainHand(),
            Constants.Blocks.POWER_CONNECTOR) && start.getWorld().equals(end.getWorld())) {
      return start.distanceSquared(location) <= distanceSquared ||
          end.distanceSquared(location) <= distanceSquared;
    } else {
      return false;
    }
  }
}
