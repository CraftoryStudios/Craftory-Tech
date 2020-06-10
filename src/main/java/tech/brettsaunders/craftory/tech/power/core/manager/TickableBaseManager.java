package tech.brettsaunders.craftory.tech.power.core.manager;

import java.util.ArrayList;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class TickableBaseManager {

  ArrayList<ITickable> tickableSlowUpdate;
  ArrayList<ITickable> tickableFastUpdate;

  public TickableBaseManager() {
    tickableSlowUpdate = new ArrayList<>();
    tickableFastUpdate = new ArrayList<>();
    Craftory plugin = Craftory.getInstance();

    /* Slow Update */
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      for (int i = 0; i < tickableSlowUpdate.size(); i++) {
        tickableSlowUpdate.get(i).slowUpdate();
      }
    }, 0L, 4L);

    /* Fast Update */
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      for (int i = 0; i < tickableFastUpdate.size(); i++) {
        tickableFastUpdate.get(i).fastUpdate();
      }
    }, 0L, 1L);
  }

  public void addFastUpdate(ITickable object) {
    tickableFastUpdate.add(object);
  }

  public void removeFastUpdate(ITickable object) {
    tickableFastUpdate.remove(object);
  }

  public void addSlowUpdate(ITickable object) {
    tickableSlowUpdate.add(object);
  }

  public void removeSlowUpdate(ITickable object) {
    tickableSlowUpdate.remove(object);
  }


}
