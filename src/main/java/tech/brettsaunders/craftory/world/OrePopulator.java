package tech.brettsaunders.craftory.world;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.utils.Logger;

public class OrePopulator extends BlockPopulator {

  @Override
  public void populate(World world, Random random, Chunk chunk) {
    int x, y, z;
    boolean isStone;
    Block block;
    for (int i = 1; i < 15; i++) {  // Number of tries
      if (random.nextInt(100) < 60) {  // The chance of spawning
        x = random.nextInt(15);
        z = random.nextInt(15);
        y = random.nextInt(40)+20;  // Get randomized coordinates
        if (chunk.getBlock(x, y, z).getType() == Material.STONE && !Craftory.customBlockManager.isCustomBlock(chunk.getBlock(x, y, z).getLocation())) {
          isStone = true;
          while (isStone) {
            block = chunk.getBlock(x, y, z);
            block = Craftory.customBlockManager.getCustomBlock("copper_ore", block);
            Logger.info("Spawned copper ore");
            if (random.nextInt(100) < 40)  {   // The chance of continuing the vein
              switch (random.nextInt(5)) {  // The direction chooser
                case 0: x++; break;
                case 1: y++; break;
                case 2: z++; break;
                case 3: x--; break;
                case 4: y--; break;
                case 5: z--; break;
              }
              x = Utilities.clamp(x,0,15);
              y = Utilities.clamp(y,0,15);
              z = Utilities.clamp(z,0,15);
              isStone = (chunk.getBlock(x, y, z).getType() == Material.STONE) && (!Craftory.customBlockManager.isCustomBlock(chunk.getBlock(x, y, z).getLocation()));
            } else isStone = false;
          }
        }
      }
    }
  }
}
