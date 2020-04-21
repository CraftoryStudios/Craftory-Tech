package tech.brettsaunders.extended;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.ChatColor;
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
    if (!(ItemsAdder.matchCustomItemName(e.getItem(), "itemsadder:ketchup_bottle"))) return;
    if (Extended.beltManagers.get(e.getClickedBlock().getLocation()) == null) return;
    e.getPlayer().sendMessage(ChatColor.RED + "Manager: " + ChatColor.WHITE + Extended.beltManagers.get(e.getClickedBlock().getLocation()));
    Extended.beltManagers.get(e.getClickedBlock().getLocation()).getTree().getParents().forEach((key,value) -> {
      e.getPlayer().sendMessage("-- KEY: " + key.toString());
    });

    e.getPlayer().sendMessage(ChatColor.RED + "Root:  " + ChatColor.WHITE + Extended.beltManagers.get(e.getClickedBlock().getLocation()).getTree().getRoot().toString());
    e.getPlayer().sendMessage(ChatColor.RED + "Parents:  " + ChatColor.WHITE + Extended.beltManagers.get(e.getClickedBlock().getLocation()).getTree().getParents().size());
    Extended.beltManagers.get(e.getClickedBlock().getLocation()).getTree().print(e.getPlayer());


    //e.getPlayer().sendMessage("Location: " + e.getClickedBlock().getLocation().toString());
  }

}