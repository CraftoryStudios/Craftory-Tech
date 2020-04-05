package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.block.Block;

public class BlockUtils {

  public boolean isCustomBlockType(Block block, String type) {
    if (ItemsAdder.isCustomBlock(block)) {
      if (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(block)).equals(type)) {
        return true;
      }
    }
    return false;
  }

}
