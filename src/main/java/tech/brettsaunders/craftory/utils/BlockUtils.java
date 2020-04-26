package tech.brettsaunders.craftory.utils;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.belts.BeltManager;
import tech.brettsaunders.craftory.tech.belts.BeltManagerContext;
import tech.brettsaunders.craftory.tech.belts.BeltManagerContext.Side;

public class BlockUtils {

  final List<String> BELT_SOUTH = Arrays
      .asList("craftory:beltsouthns", "craftory:beltsouths", "craftory:beltsouthn",
          "craftory:beltsouth");
  final List<String> BELT_NORTH = Arrays
      .asList("craftory:beltns", "craftory:belts", "craftory:beltn", "craftory:belt");
  final List<String> BELT_EAST = Arrays
      .asList("craftory:belteastew", "craftory:belteastee", "craftory:belteastw",
          "craftory:belteast");
  final List<String> BELT_WEST = Arrays
      .asList("craftory:beltwestew", "craftory:beltweste", "craftory:beltwestw",
          "craftory:beltwest");
  final List<List> BELTS = Arrays.asList(BELT_SOUTH, BELT_NORTH, BELT_EAST, BELT_WEST);

  public boolean isCustomBlockType(Block block, String type) {
    if (ItemsAdder.isCustomBlock(block)) {
      return ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(block)).equals(type);
    }
    return false;
  }

  public boolean isBelt(Block block, BlockFace direction) {
    if (direction == BlockFace.EAST) {
      return checkIsBelt(block, BELT_EAST);
    } else if (direction == BlockFace.WEST) {
      return checkIsBelt(block, BELT_WEST);
    } else if (direction == BlockFace.NORTH) {
      return checkIsBelt(block, BELT_NORTH);
    } else if (direction == BlockFace.SOUTH) {
      return checkIsBelt(block, BELT_SOUTH);
    } else {
      Craftory.plugin.getLogger().warning("Belt direction doesn't exist");
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
    for (String belt : belts) {
      if (isCustomBlockType(block, belt)) {
        return true;
      }
    }
    return false;
  }

  public void onBeltPlace(Block block, BlockFace behind, BlockFace left, BlockFace right,
      BlockFace front) {
    ArrayList<BeltManagerContext> currentBeltManagers = new ArrayList<>();
    Block blockBehind = block.getRelative(behind);
    Block blockLeft = block.getRelative(left);
    Block blockRight = block.getRelative(right);
    Block blockFront = block.getRelative(front);

    //Front Sideways Right
    if (isBelt(blockFront, right) && hasBeltManager(blockFront)) {
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockFront), Side.SidewaysRight, blockFront));
      Bukkit.getLogger().info("Front Sideways Right");
    }
    //Front Sideways Left
    if (isBelt(blockFront, left) && hasBeltManager(blockFront)) {
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockFront), Side.SidewaysLeft, blockFront));
      Bukkit.getLogger().info("Front Sideways Left");
    }
    //Back
    if (isBelt(blockBehind, front) && hasBeltManager(blockBehind)) {
      Bukkit.getLogger().info("Behind");
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockBehind), Side.Back, blockBehind));
    }
    //Front
    if (isBelt(blockFront, front) && hasBeltManager(blockFront)) {
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockFront), Side.Front, blockFront));
      Bukkit.getLogger().info("Front");
    }
    //Left
    if (isBelt(blockLeft, right) && hasBeltManager(blockLeft)) {
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockLeft), Side.Left, blockLeft));
      Bukkit.getLogger().info("Left");
    }
    //Right
    if (isBelt(blockRight, left) && hasBeltManager(blockRight)) {
      currentBeltManagers
          .add(new BeltManagerContext(getBeltManager(blockRight), Side.Right, blockRight));
      Bukkit.getLogger().info("Right");
    }

    Bukkit.getLogger().info("LIST OF " + currentBeltManagers.toString());
    Bukkit.getLogger().info("SIZE" + currentBeltManagers.size());
    BeltManagerContext leadManager = null;
    if (currentBeltManagers.size() >= 1) {
      for (BeltManagerContext context : currentBeltManagers) {
        Bukkit.getLogger().info(context.getBeltManager().getLength() + " length");
      }
      Collections.sort(currentBeltManagers);

      leadManager = currentBeltManagers.remove(0);
    }

    if (leadManager == null) {
      leadManager = new BeltManagerContext(new BeltManager(block), Side.None);
      Bukkit.getLogger().info("NEW MANAGER");
    } else {
      leadManager.getBeltManager()
          .addBelt(block, leadManager.getSide(), leadManager.getBlock(), currentBeltManagers);
      Bukkit.getLogger().info("OLD MANAGER");
    }
    Bukkit.getLogger().warning("----------------------------------------");
  }

  public void onNorthBeltPlace(Block block) {
    onBeltPlace(block, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH);
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
    return Craftory.beltManagers.containsKey(block.getLocation());
  }

  public BeltManager getBeltManager(Block block) {
    return Craftory.beltManagers.get(block.getLocation());
  }

}

