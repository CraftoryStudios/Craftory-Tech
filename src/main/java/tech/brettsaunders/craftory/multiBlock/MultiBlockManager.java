package tech.brettsaunders.craftory.multiBlock;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.utils.Logger;

public class MultiBlockManager {

  private final String toolName = "tool";
  private final ArrayList<String> controlBlockTypes = new ArrayList<>(
      Arrays.asList("machine_core", "magic_core"));
  private final ArrayList<MultiBlock> multiBlockTypes = new ArrayList<>(Arrays.asList());
  private ArrayList<MultiBlock> multiBlocks;
  private String SAVE_PATH = "MultiBlock.data";

  public MultiBlockManager(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    MutliBlockData data;
    multiBlocks = new ArrayList<>();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      data = (MutliBlockData) in.readObject();
      multiBlocks = data.multiBlocks;
      in.close();
      Logger.info("*** MultiBlockManager Loaded");
    } catch (IOException e) {
      Logger.warn("*** New MultiBlockManager Created");
      Logger.debug(e.toString());
    } catch (Exception e) {
      Logger.debug(e.toString());
    }
  }

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

  public void save() {
    MutliBlockData data = new MutliBlockData(multiBlocks);
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));
      out.writeObject(data);
      out.close();
      Logger.info("Barrel Saved");
    } catch (IOException e) {
      e.printStackTrace();
      Logger.warn("Barrel failed to save " + e);
    }
  }

  private class MutliBlockData {

    protected ArrayList<MultiBlock> multiBlocks;

    public MutliBlockData(ArrayList<MultiBlock> multiBlocks) {
      this.multiBlocks = multiBlocks;
    }
  }
}
