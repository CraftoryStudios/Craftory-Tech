package tech.brettsaunders.craftory.utils;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperItemMovement {

  private static final BlockFace[] inputDirections = {BlockFace.NORTH, BlockFace.EAST,
      BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};


  public static boolean moveItemsOut(Location location, ItemStack slot) {
    boolean moved = false;
    if (slot != null) {
      //Only do if there is something to output
      Block b = location.getBlock().getRelative(BlockFace.DOWN);
      if (b.getType().equals(Material.HOPPER)) {
        ItemStack toMove = slot.clone();
        toMove.setAmount(1);
        Inventory hopperInventory = ((Hopper) b.getState()).getInventory();
        HashMap<Integer, ItemStack> failedItems = hopperInventory.addItem(toMove);
        if (failedItems.isEmpty()) {
          slot.setAmount(slot.getAmount() - 1);
        } else {
          moved = true;
        }
      }
    }
    return moved;
  }

  /**
   * Checks nearby hoppers and moves the items into the slot, if possible
   *
   * @param location The location of the block
   * @param slot The ItemSlot to move them into
   * @return The item slot with the new items in, OR null if items couldn't be inserted
   */
  public static ItemStack moveItemsIn(Location location, ItemStack slot) {
    boolean moved = false;
    Block b;
    ItemStack[] hopperItems;
    BlockFace facing;
    if (slot == null || slot.getAmount() < slot.getMaxStackSize()) {
      for (BlockFace face : inputDirections) {
        b = location.getBlock().getRelative(face);
        if (b.getType().equals(Material.HOPPER)) {
          facing = ((Directional) b.getBlockData()).getFacing();
          if (!facing.equals(face.getOppositeFace())) {
            continue; //Skip if hopper is not facing block
          }
          hopperItems = ((Hopper) b.getState()).getInventory().getContents();
          for (ItemStack item : hopperItems) {
            if (item == null) {
              continue;
            }
            if (slot == null) {
              slot = item.clone();
              slot.setAmount(1);
              item.setAmount(item.getAmount() - 1);
              moved = true;
              break;
            } else if (slot.getType().toString().equals(item.getType().toString())
                && slot.getAmount() < slot.getMaxStackSize()) {
              slot.setAmount(slot.getAmount() + 1);
              item.setAmount(item.getAmount() - 1);
              moved = true;
              break;
            }
          }
        }
      }
    }
    if (moved) {
      return slot;
    }
    return null;
  }
}
