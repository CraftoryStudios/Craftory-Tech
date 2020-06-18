package tech.brettsaunders.craftory.tech.power.core.manager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class TickableBaseManager {

  final ArrayList<ITickable> tickableUpdate;
  public TickableBaseManager() {
    tickableUpdate = new ArrayList<>();
    Craftory plugin = Craftory.plugin;
    AtomicLong worldTime = new AtomicLong();

    /* Update */
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      worldTime.getAndIncrement();
      for (ITickable iTickable : tickableUpdate) {
        iTickable.update(worldTime.get());
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
