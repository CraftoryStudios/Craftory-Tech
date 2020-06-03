package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

/**
 * Energy Cell
 *
 * Capacity: 20,000,000
 * Max Input: 8000
 * Max Output: 8000
 * Level: 2 (DIAMOND)
 */
public class DiamondCell extends BaseCell {

  public DiamondCell(Location location) {
    super(location);
    level = 2;
    //TODO find a way to not redeclare this to update level
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
  }

  public DiamondCell() {
    super();
  }

}
