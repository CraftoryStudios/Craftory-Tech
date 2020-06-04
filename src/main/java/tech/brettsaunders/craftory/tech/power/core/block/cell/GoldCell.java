package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;

/**
 * Energy Cell
 *
 * Capacity: 2,000,000
 * Max Input: 800
 * Max Output: 800
 * Level: 1 (GOLD)
 */
public class GoldCell extends BaseCell {
  private static final long serialVersionUID = 10014L;
  private static final byte CLEVEL = 1;
  private static final int C_OUTPUT_AMOUNT = 800;

  public GoldCell(Location location) {
    super(location, CLEVEL, C_OUTPUT_AMOUNT);
  }

  public GoldCell() {
    super();
  }

}
