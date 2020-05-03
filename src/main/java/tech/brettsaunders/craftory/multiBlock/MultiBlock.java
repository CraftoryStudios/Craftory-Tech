package tech.brettsaunders.craftory.multiBlock;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

public abstract class MultiBlock {

  //x,y,z
  // I think this would be a 3x3 grid of stone horizontally with control block being centre block
  private static final String[][][] recipe =
      {{{"minecraft:stone", "minecraft:stone", "minecraft:stone"}},
          {{"minecraft:stone", "minecraft:stone", "minecraft:stone"}},
          {{"minecraft:stone", "minecraft:stone", "minecraft:stone"}}};
  private final int controlX = 1;
  private final int controlY = 0;
  private final int controlZ = 1;
  private Location controlBlockLocation;
  private static final String controlBlockType = "machine_core";
  private final ArrayList<Location> activeBlocks = new ArrayList<>();

  public MultiBlock(Block controlBlock) {
    controlBlockLocation = controlBlock.getLocation();
  }

  public void setControlBlock(Location location) {
    controlBlockLocation = location;
  }

  public void setControlBlock(Block block) {
    controlBlockLocation = block.getLocation();
  }

  public ArrayList<Location> getActiveBlocks() {
    return activeBlocks;
  }

  public boolean checkValid(Block controlBlock) {
    String type =
        (ItemsAdder.isCustomBlock(controlBlock)) ? ItemsAdder.getCustomBlock(controlBlock).getType()
            .toString() : controlBlock.getType().toString();
    if (!type.equals(controlBlockType)) {
      return false;
    }
    return checkValid(controlBlock.getWorld());
  }

  public boolean checkValid(World world) {
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

  public boolean checkValid() {
    return checkValid(controlBlockLocation.getWorld());
  }

  /**
   * When an event is detected involving one of the active blocks (this contains the functionality
   * of the structure
   *
   * @param e The event
   */
  public void activeBlockEvent(Event e) {

  }

  /**
   * Form the multi-block structure into its full form
   */
  public void form() {
    if (!checkValid()) {
      return;
    }
    //Do all the texture and visual stuff
    createVisuals();
    //Add the blocks the structure uses to the active blocks list
    setActiveBlocks();
  }

  protected abstract void createVisuals();

  protected abstract void setActiveBlocks();

  public MultiBlock clone(Block controlBlock) {
    return this.clone(controlBlock);
  }
}
