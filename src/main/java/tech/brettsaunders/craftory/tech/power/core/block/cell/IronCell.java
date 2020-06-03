package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;

/**
 * Energy Cell
 *
 * Capacity: 400,000
 * Max Input: 200
 * Max Output: 200
 * Level: 0 (IRON)
 */
public class IronCell extends BaseCell {


  public IronCell(Location location) {
    super(location);
  }

  public IronCell() {
    super();
  }

}
