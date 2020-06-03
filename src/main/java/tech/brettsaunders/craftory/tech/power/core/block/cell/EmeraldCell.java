package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;

/**
 * Energy Cell
 *
 * Capacity: 80,000,000
 * Max Input: 32,000
 * Max Output: 32,000
 * Level: 3 (Emerald)
 */
public class EmeraldCell extends BaseCell {
  private static final byte CLEVEL = 3;
  private static final int C_OUTPUT_AMOUNT = 32000;

  public EmeraldCell(Location location) {
    super(location, CLEVEL, C_OUTPUT_AMOUNT);
  }

  public EmeraldCell() {
    super();
  }

}
