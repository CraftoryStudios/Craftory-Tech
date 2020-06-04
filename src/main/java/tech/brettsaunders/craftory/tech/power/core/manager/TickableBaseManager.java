package tech.brettsaunders.craftory.tech.power.core.manager;

import java.util.ArrayList;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class TickableBaseManager {
  ArrayList<ITickable> tickableObjects;

  public TickableBaseManager() {
    tickableObjects = new ArrayList<>();
    Craftory plugin = Craftory.getInstance();
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      for (int i = 0; i < tickableObjects.size(); i++) {
        tickableObjects.get(i).update();
      }
    }, 0L, 1L);
  }

  public void addBaseTickable(ITickable object) {
    tickableObjects.add(object);
  }

  public void removeBaseTickable(ITickable object) {
    tickableObjects.remove(object);
  }


}
