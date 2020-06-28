package tech.brettsaunders.craftory.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.Craftory;

public class OrePopulator extends BlockPopulator {

  @Override
  public void populate(World world, Random random, Chunk chunk) {
    spawnOre(random, chunk, Blocks.COPPER_ORE, 8, 60, 3, 6, 10, 60);
  }

  private void spawnCommonOre(Random r, Chunk chunk, String ore) {
    spawnOre(r, chunk, ore, 10, 60, 3, 5, 20, 75);
  }

  private void spawnRareOre(Random r, Chunk chunk, String ore) {
    spawnOre(r, chunk, ore, 2, 50, 1, 3, 5, 20);
  }

  private void spawnOre(Random r, Chunk chunk, String ore, int attempts, int chance,
      int minVeinSize, int maxVeinSize, int minHeight, int maxHeight) {
    int x, y, z;
    Block block;
    boolean valid;
    ArrayList<Integer> options;
    for (int i = 0; i < attempts; i++) {
      if (r.nextInt(100) < chance) {
        x = r.nextInt(16);
        z = r.nextInt(16);
        y = minHeight + r.nextInt(maxHeight - minHeight);
        block = chunk.getBlock(x, y, z);
        if (validBlock(block)) {
          Craftory.customBlockManager.getCustomBlockOfItem(ore, block);
          for (int j = 0; j < maxVeinSize; j++) {
            if (j >= minVeinSize && r.nextInt(100) < 40) {
              break;
            }
            int xx, yy, zz;
            options = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
            valid = false;
            while (options.size() > 0) {
              xx = x;
              yy = y;
              zz = z;
              switch (options.remove(r.nextInt(options.size()))) {
                case 0:
                  x++;
                  break;
                case 1:
                  y++;
                  break;
                case 2:
                  z++;
                  break;
                case 3:
                  x--;
                  break;
                case 4:
                  y--;
                  break;
                case 5:
                  z--;
                  break;
              }
              if (x < 0 || x > 15 || y < minHeight || y > maxHeight || z < 0 || z > 15 || (!validBlock(block = chunk.getBlock(x, y, z)))) {
                x = xx;
                y = yy;
                z = zz;
              } else {
                valid = true;
                break;
              }
            }
            if (valid) {
              Craftory.customBlockManager.getCustomBlockOfItem(ore, block);
            } else {
              break;
            }
          }
        }
      }
    }
  }

  private boolean validBlock(Block block) {
    //if(Craftory.customBlockManager.isCustomBlock(block.getLocation())) return false;
    Material type = block.getType();
    return type.equals(Material.STONE) || type.equals(Material.DIORITE) || type
        .equals(Material.ANDESITE) || type.equals(Material.GRANITE);
  }
}
