package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

/**
 * Energy Cell
 *
 * Capacity: 2,000,000
 * Max Input: 800
 * Max Output: 800
 * Level: 1 (GOLD)
 */
public class GoldCell extends BaseCell {

  public GoldCell(Location location) {
    super(location);
    level = 1;
    //TODO find a way to not redeclare this to update level
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
  }

  public GoldCell() {
    super();
  }

}
