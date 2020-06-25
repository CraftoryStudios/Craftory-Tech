package tech.brettsaunders.craftory.tech.power.core.powerGrid;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;

public class PowerGridManagerV2 implements Listener {

  public static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  private static final String DATA_PATH;

  static {
    DATA_PATH = Utilities.DATA_FOLDER + File.separator + "poweredBlock.data";
  }

  private final HashMap<Location, PowerGridManager> powerGrids;
  public HashSet<PowerGridManager> powerGridManagers;

  public PowerGridManagerV2() {
    powerGridManagers = new HashSet<>();
    powerGrids = new HashMap<>();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onPoweredBlockBreak(CustomBlockBreakEvent event) {
    Location location = event.getLocation();
    if (powerGrids.containsKey(location)) { //GRID / Power connector stuff
      Craftory.powerConnectorManager.destroyBeams(location);
      if (powerGrids.get(location).getGridSize() > 1) {
        List<PowerGridManager> newGrids = powerGrids.get(location).splitGrids(location);
        for (Location l : powerGrids.get(location).powerConnectors.keySet()) {
          powerGrids.remove(l);
        }
        for (PowerGridManager grid : newGrids) {
          for (Location loc : grid.powerConnectors.keySet()) {
            powerGrids.put(loc, grid);
          }
        }
      }
      powerGrids.remove(location);
    }

    if (!poweredBlocks.containsKey(location)) {
      return;
    }
    Craftory.powerConnectorManager.destroyBeams(location); //Destroy any beams
  }

  //TODO CLEAN UP


  public void mergeGrids(PowerGridManager old, PowerGridManager merged) {
    for (HashMap.Entry<Location, PowerGridManager> entry : powerGrids.entrySet()) {
      if (entry.getValue().equals(old)) {
        powerGrids.put(entry.getKey(), merged);
      }
    }
  }

  private void getAdjacentPowerBlocks(Location location, PowerGridManager powerGridManager) {
    Location blockLocation;
    for (BlockFace face : faces) {
      blockLocation = location.getBlock().getRelative(face).getLocation();
      if (Craftory.customBlockManager.isCustomBlock(blockLocation) && poweredBlocks
          .containsKey(blockLocation)) {
        if (isCell(blockLocation)) {
          powerGridManager.addPowerCell(location, blockLocation);
        } else if (isGenerator(blockLocation)) {
          powerGridManager.addGenerator(location, blockLocation);
        } else if (isMachine(blockLocation)) {
          powerGridManager.addMachine(location, blockLocation);
        }
      }
    }
  }

  private void addPowerGridManager(Location location, PowerGridManager manger) {
    powerGridManagers.add(manger);
    powerGrids.put(location, manger);
    //TODO for every merge or place of a power connector
    //TODO when merge change this
  }

  public PowerGridManager getPowerGridManager(Location location) {
    return powerGrids.get(location);
  }

  private static class PowerBlockManagerData implements Serializable {

    private static final long serialVersionUID = 9999L;
    protected final HashSet<PowerGridManager> powerGridManagers;

    public PowerBlockManagerData(HashMap<Location, PoweredBlock> poweredBlocks,
        HashSet<PowerGridManager> powerGridManagers) {
      this.powerGridManagers = powerGridManagers;
    }
  }

}
