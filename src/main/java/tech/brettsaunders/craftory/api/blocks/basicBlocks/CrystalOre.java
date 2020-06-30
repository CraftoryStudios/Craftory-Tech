package tech.brettsaunders.craftory.api.blocks.basicBlocks;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class CrystalOre extends CustomBlock {
  public CrystalOre() {super();}
  public CrystalOre(Location location) {
    super(location, Blocks.CRYSTAL_ORE);
  }
}
