package tech.brettsaunders.craftory.api.blocks.basicBlocks;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;

public class PowerConnector extends CustomBlock {
  public PowerConnector() {super();}
  public PowerConnector(Location location) {
    super(location, Blocks.POWER_CONNECTOR);
  }
}
