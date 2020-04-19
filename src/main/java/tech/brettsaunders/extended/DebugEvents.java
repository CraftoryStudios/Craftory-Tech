package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebugEvents implements Listener {
  BlockUtils blockUtils = new BlockUtils();

  @EventHandler
  public void onPlayerRightClickDebug(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.LEFT_CLICK_BLOCK)) return;
    if (!(e.hasItem())) return;
    if (!(ItemsAdder.matchCustomItemName(e.getItem(), "itemsadder:ketchup_bottle")));

    e.getPlayer().sendMessage("Manager: " + Extended.beltManagers.getMap().get(e.getClickedBlock().getLocation()));
  }

}
