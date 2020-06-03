package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

/**
 * Energy Cell
 *
 * Capacity: 80,000,000
 * Max Input: 32,000
 * Max Output: 32,000
 * Level: 3 (Emerald)
 */
public class EmeraldCell extends BaseCell {

  public EmeraldCell(Location location) {
    super(location);
    level = 3;
    //TODO find a way to not redeclare this to update level
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
  }

  public EmeraldCell() {
    super();
  }

}
