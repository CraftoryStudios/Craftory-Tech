package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;

/**
 * Energy Cell
 * <p>
 * Capacity: 400,000 Max Input: 200 Max Output: 200 Level: 0 (IRON)
 */
public class IronCell extends BaseCell {

  /* Static Constants Private */
  private static final long serialVersionUID = 10015L;
  private static final byte C_LEVEL = 0;
  private static final int C_OUTPUT_AMOUNT = 200;

  /* Construction */
  public IronCell(Location location) {
    super(location, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public IronCell() {
    super();
  }

}
