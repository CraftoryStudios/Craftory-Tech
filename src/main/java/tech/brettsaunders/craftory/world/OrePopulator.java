package tech.brettsaunders.craftory.world;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.utils.Logger;

public class OrePopulator extends BlockPopulator {

  @Override
  public void populate(World world, Random random, Chunk chunk) {
    spawnOre(random,chunk,"copper_ore",10,60,3,6,10,60);
  }

  private void spawnCommonOre(Random r, Chunk chunk, String ore){
    spawnOre(r, chunk, ore, 10, 60, 3,5,20,75);
  }

  private void spawnRareOre(Random r, Chunk chunk, String ore) {
    spawnOre(r, chunk,ore,2,50,1,3,5,20);
  }

  private void spawnOre(Random r, Chunk chunk, String ore, int attempts, int chance, int minVeinSize, int maxVeinSize, int minHeight, int maxHeight) {
    int x,y,z;
    int xx,yy,zz;
    Block block;
    boolean valid;
    for(int i = 0; i < attempts; i++) {
      if(r.nextInt(100) < chance) {
        x = r.nextInt(16);
        z = r.nextInt(16);
        y = minHeight + r.nextInt(maxHeight-minHeight);
        block = chunk.getBlock(x,y,z);
        if(valid = validBlock(block)){
          Craftory.customBlockManager.getCustomBlock(ore,block);
          for (int j = 0; j < maxVeinSize; j++) {
            if(j >= minVeinSize && r.nextInt(100) < 33) break;
            for (int k = 0; k < 12; k++) {
              xx = x;
              yy = y;
              zz = z;
              switch (r.nextInt(6)) {  // The direction chooser
                case 0: x++; break;
                case 1: y++; break;
                case 2: z++; break;
                case 3: x--; break;
                case 4: y--; break;
                case 5: z--; break;
              }
              if(x<0||x>15||y<0||y>15||z<0||z>15||(!validBlock(block = chunk.getBlock(x,y,z)))){
                x = xx;
                y = yy;
                z = zz;
                valid = false;
              } else {
                valid = true;
               break;
              }
            }
            if(valid){
              Logger.info("Spawned " + j + " block in vein");
              Craftory.customBlockManager.getCustomBlock(ore,block);
            }
            else {
              Logger.info("Failed to find any locations " + j);
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
    return type.equals(Material.STONE) || type.equals(Material.DIORITE) || type.equals(Material.ANDESITE) || type.equals(Material.GRANITE);
  }
}
