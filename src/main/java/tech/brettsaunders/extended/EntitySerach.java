package tech.brettsaunders.extended;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntitySerach implements Runnable {
  final double VELOCITY = 0.1;
  BlockUtils blockUtils = new BlockUtils();

  @Override
  public void run() {
    for (World world : Extended.plugin.getServer().getWorlds()) {
      List<Player> players = world.getPlayers();
      // No players in this world
      if (players.isEmpty()) {
        continue;
      }

      for (Entity entity : world.getEntitiesByClass(Entity.class)) {
        Location entityLocation = entity.getLocation();

        if (entity.isDead()) {
          continue;
        }

        Block blockUnder = entityLocation.getBlock().getRelative(BlockFace.DOWN);
        if (!blockUtils.isCustomBlockType(blockUnder, "extended:belt")) {
          continue;
        }

        if (entity instanceof Player) {
          Player player = (Player) entity;
          // Pressing shift stops the movement
          if (player.isSneaking()) {
            continue;
          }
        }
        entity.setVelocity(entity.getVelocity().add(new Vector(0,0,-VELOCITY)));
        //entity.setVelocity(new Vector(0,0,-VELOCITY));
      }
    }
  }
}

