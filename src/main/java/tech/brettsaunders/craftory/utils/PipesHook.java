package tech.brettsaunders.craftory.utils;

import de.robotricker.transportpipes.api.TransportPipesAPI;
import de.robotricker.transportpipes.location.BlockLocation;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.pipes.PipeContainer;

public class PipesHook {

  public static void addPipeContainer(Location location, Map<BlockFace, Integer> inputFace, ArrayList<Integer> outputLocations, Inventory inventory) {
    // Register all pipe containers on first tick when Transport Pipes enabled
      Tasks.runTaskLater(() -> {
        try {
          TransportPipesAPI.getInstance().registerTransportPipesContainer(new PipeContainer(location, inputFace, outputLocations, inventory),
              new BlockLocation(location),
              location.getWorld());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }, 1L);

  }

  public static void removePipeContainer(Location location) throws Exception {
    TransportPipesAPI.getInstance().unregisterTransportPipesContainer(new BlockLocation(location), location.getWorld());
  }
}
