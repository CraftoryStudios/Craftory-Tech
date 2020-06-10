package tech.brettsaunders.craftory.utils;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.block.Block;

public class BlockUtils {

  public static boolean isCustomTypeBlock(Block block, String type) {
    if (ItemsAdder.isCustomBlock(block)) {
      return ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(block)).equals(type);
    }
    return false;
  }

}

