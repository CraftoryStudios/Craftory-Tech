package tech.brettsaunders.craftory;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.mcMMO;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class McMMOListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onItemSalvage(McMMOPlayerSalvageCheckEvent e) {
    if (e.getSalvageItem() != null && CustomItemManager.isCustomItem(e.getSalvageItem(), true)){
      e.setCancelled(true);
    }
  }

}
