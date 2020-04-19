package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.extended.BeltManagerContext.Side;

public class BlockUtils {

  final List<String> BELT_SOUTH = Arrays.asList("extended:beltsouthns", "extended:beltsouths", "extended:beltsouthn", "extended:beltsouth");
  final List<String> BELT_NORTH = Arrays.asList("extended:beltns", "extended:belts", "extended:beltn", "extended:belt");
  final List<String> BELT_EAST = Arrays.asList("extended:belteastew", "extended:belteastee", "extended:belteastw", "extended:belteast");
  final List<String> BELT_WEST = Arrays.asList("extended:beltwestew", "extended:beltweste", "extended:beltwestw", "extended:beltwest");
  final List<List> BELTS = Arrays.asList(BELT_SOUTH, BELT_NORTH, BELT_EAST, BELT_WEST);

  public boolean isCustomBlockType(Block block, String type) {
    if (ItemsAdder.isCustomBlock(block)) {
      if (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(block)).equals(type)) {
        return true;
      }
    }
    return false;
  }

  public boolean isBelt(Block block, BlockFace direction) {
    if (direction == BlockFace.EAST) {
      if (checkIsBelt(block, BELT_EAST)) {
        return true;
      }
    } else if (direction == BlockFace.WEST) {
      if (checkIsBelt(block, BELT_WEST)) {
        return true;
      }
    } else if (direction == BlockFace.NORTH) {
      if (checkIsBelt(block, BELT_NORTH)) {
        return true;
      }
    } else if (direction == BlockFace.SOUTH) {
      if (checkIsBelt(block, BELT_SOUTH)) {
        return true;
      }
    } else {
      Extended.plugin.getLogger().warning("Belt direction doesn't exist");
    }
    return false;
  }

  public boolean isBelt(Block block) {
    for (List<String> belt : BELTS) {
      if (checkIsBelt(block, belt)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkIsBelt(Block block, List<String> belts) {
    for (String belt: belts) {
      if (isCustomBlockType(block, belt)) {
        return true;
      }
    }
    return false;
  }

  public void onBeltPlace(Block block, BlockFace behind, BlockFace left, BlockFace right, BlockFace front) {
    ArrayList<BeltManagerContext> currentBeltManagers = new ArrayList<>();
    Block blockBehind = block.getRelative(behind);
    Block blockLeft = block.getRelative(left);
    Block blockRight = block.getRelative(right);
    Block blockFront = block.getRelative(front);

    if (isBelt(blockBehind, front) && hasBeltManager(blockBehind)) {
      currentBeltManagers.add(new BeltManagerContext(getBeltManager(blockBehind), Side.Back, blockBehind));
    }
    if (isBelt(blockFront, front) && hasBeltManager(blockFront)) {
      currentBeltManagers.add(new BeltManagerContext(getBeltManager(blockFront), Side.Front, blockFront));
    }
    if (isBelt(blockLeft, right) && hasBeltManager(blockLeft)) {
      currentBeltManagers.add(new BeltManagerContext(getBeltManager(blockLeft), Side.Left, blockLeft));
    }
    if (isBelt(blockRight, left) && hasBeltManager(blockRight)) {
      currentBeltManagers.add(new BeltManagerContext(getBeltManager(blockRight), Side.Right, blockRight));
    }

    if (currentBeltManagers.size() > 1) {
      Collections.sort(currentBeltManagers);
    }

    BeltManagerContext leadManager = currentBeltManagers.remove(0);

    if (leadManager == null) {
      leadManager = new BeltManagerContext(new BeltManager(block), Side.None);
    } else {
      leadManager.getBeltManager().addBelt(block, leadManager.getSide(), leadManager.getBlock(), currentBeltManagers);
    }
  }

  public void onNorthBeltPlace(Block block) {
    onBeltPlace(block, BlockFace.SOUTH,BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH);
  }

  public void onWestBeltPlace(Block block) {
    onBeltPlace(block, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST);
  }

  public void onEastBeltPlace(Block block) {
    onBeltPlace(block, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST);
  }

  public void onSouthBeltPlace(Block block) {
    onBeltPlace(block, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH);
  }

  public Boolean hasBeltManager(Block block) {
    return Extended.beltManagers.getMap().containsKey(block.getLocation());
  }

  public BeltManager getBeltManager(Block block) {
    return Extended.beltManagers.getMap().get(block.getLocation());
  }

}

