package tech.brettsaunders.extended;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Chunk;
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

        Chunk chunk = world.getChunkAt(entityLocation);

        if (!Extended.chunkKeys.contains((((long) chunk.getX()) << 32) | (chunk.getZ() & 0xFFFFFFFFL))) {
          continue;
        }

        if (entity instanceof Player) {
          Player player = (Player) entity;
          // Pressing shift stops the movement
          ///player.sendMessage(world.getChunkAt(entityLocation).toString());
          if (player.isSneaking()) {
            continue;
          }
        }

        Block blockUnder = entityLocation.getBlock().getRelative(BlockFace.DOWN);


        if (blockUtils.isBelt(blockUnder, BlockFace.NORTH)) {
          entity.setVelocity(entity.getVelocity().add(new Vector(0,0,-VELOCITY)));
        }
        if (blockUtils.isBelt(blockUnder, BlockFace.EAST)) {
          entity.setVelocity(entity.getVelocity().add(new Vector(VELOCITY,0,0)));
        }
        if (blockUtils.isBelt(blockUnder, BlockFace.SOUTH)) {
          entity.setVelocity(entity.getVelocity().add(new Vector(0,0,VELOCITY)));
        }
        if (blockUtils.isBelt(blockUnder, BlockFace.WEST)) {
          entity.setVelocity(entity.getVelocity().add(new Vector(-VELOCITY,0,0)));
        }


        //entity.setVelocity(new Vector(0,0,-VELOCITY));
      }
    }
  }
}

