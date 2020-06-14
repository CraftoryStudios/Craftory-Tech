package tech.brettsaunders.craftory.tech.power.core.manager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class TickableBaseManager {

  ArrayList<ITickable> tickableUpdate;
  public TickableBaseManager() {
    tickableUpdate = new ArrayList<>();
    Craftory plugin = Craftory.getInstance();
    AtomicLong worldTime = new AtomicLong();

    /* Update */
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      worldTime.getAndIncrement();
      for (int i = 0; i < tickableUpdate.size(); i++) {
        tickableUpdate.get(i).update(worldTime.get());
      }
    }, 0L, 1L);
  }

  public void addUpdate(ITickable object) {
    tickableUpdate.add(object);
  }

  public void removeUpdate(ITickable object) {
    tickableUpdate.remove(object);
  }

}
