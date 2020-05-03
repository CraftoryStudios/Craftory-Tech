package tech.brettsaunders.craftory.multiBlock;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MultiBlockManagers {

  private final String toolName = "tool";
  ArrayList<MultiBlock> multiBlocks = new ArrayList<>();
  ArrayList<String> controlBlockTypes = new ArrayList<>(
      Arrays.asList("machine_core", "magic_core"));
  ArrayList<MultiBlock> multiBlockTypes = new ArrayList<>(Arrays.asList());

  public void PlayerInteract(PlayerInteractEvent e) {
    //Check if this has something to do with an existing multi-block structure
    Block block = e.getClickedBlock();
    Location loc = block.getLocation();
    for (MultiBlock struct : multiBlocks) {
      if (struct.getActiveBlocks().contains(loc)) {
        struct.activeBlockEvent(
            e); //If this block is in the structures active list pass the event to be dealt with.
        break;
      }
    }

    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && ItemsAdder.isCustomItem(e.getItem())
        && ItemsAdder.matchCustomItemName(e.getItem(), toolName)) {
      String type =
          (ItemsAdder.isCustomBlock(block)) ? ItemsAdder.getCustomBlock(block).getType().toString()
              : block.getType().toString();
      if (controlBlockTypes.contains(type)) {
        //Creat the multi-struct some how.
        for (MultiBlock struct : multiBlockTypes) {
          if (struct.checkValid(block)) {
            MultiBlock newStruct = struct.clone(block);
            multiBlocks.add(newStruct);
            break;
          }
        }
      }
    }

  }
}
