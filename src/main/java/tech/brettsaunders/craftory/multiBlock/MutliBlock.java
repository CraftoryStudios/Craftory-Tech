package tech.brettsaunders.craftory.multiBlock;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class MutliBlock {

  //x,y,z
  // I think this would be a 3x3 grid of stone horizontally with control block being centre block
  private final String[][][] recipe =
      {{{"minecraft:stone", "minecraft:stone", "minecraft:stone"}},
          {{"minecraft:stone", "minecraft:stone", "minecraft:stone"}},
          {{"minecraft:stone", "minecraft:stone", "minecraft:stone"}}};
  private final int controlX = 1;
  private final int controlY = 0;
  private final int controlZ = 1;
  private Location controlBlockLocation;

  public void setControlBlock(Location location) {
    controlBlockLocation = location;
  }

  public void setControlBlock(Block block) {
    controlBlockLocation = block.getLocation();
  }

  public boolean checkValid() {
    World world = controlBlockLocation.getWorld();
    for (int x = 0; x < recipe.length; x++) {
      for (int y = 0; y < recipe[0].length; y++) {
        for (int z = 0; y < recipe[0][0].length; z++) {
          Block block = world.getBlockAt(x, y, z);
          if (ItemsAdder.isCustomBlock(block)) {
            if (!ItemsAdder
                .matchCustomItemName(ItemsAdder.getCustomBlock(block), recipe[x][y][z])) {
              return false;
            }
          } else {
            if (!block.getType().toString().equals(recipe[x][y][z])) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }
}
