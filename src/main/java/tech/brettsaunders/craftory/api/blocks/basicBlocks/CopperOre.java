package tech.brettsaunders.craftory.api.blocks.basicBlocks;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class CopperOre extends CustomBlock {
  public CopperOre() {super();}
  public CopperOre(Location location) {
    super(location, Blocks.COPPER_ORE);
  }
}
