package tech.brettsaunders.craftory.tech.power.core.block;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;

public class IronCell extends BaseCell {

  public IronCell(Location location) {
    super(location);
    isReceiver = true;
  }

  public IronCell() {
    super();
    isReceiver = true;
  }

}
