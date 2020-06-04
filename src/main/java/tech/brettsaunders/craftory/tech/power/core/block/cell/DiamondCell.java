package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;

/**
 * Energy Cell
 *
 * Capacity: 20,000,000
 * Max Input: 8000
 * Max Output: 8000
 * Level: 2 (DIAMOND)
 */
public class DiamondCell extends BaseCell {
  private static final long serialVersionUID = 10012L;

  private static final byte CLEVEL = 2;
  private static final int C_OUTPUT_AMOUNT = 8000;

  public DiamondCell(Location location) {
    super(location, CLEVEL, C_OUTPUT_AMOUNT);
  }

  public DiamondCell() {
    super();
  }

}
