package tech.brettsaunders.craftory.tech.belts;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import tech.brettsaunders.craftory.Craftory;

public class DebugEvents implements Listener {

  @EventHandler
  public void onPlayerRightClickDebug(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (!(e.hasItem())) {
      return;
    }
    if (!(ItemsAdder.matchCustomItemName(e.getItem(), "itemsadder:ketchup_bottle"))) {
      return;
    }
    if (Craftory.beltManagers.get(e.getClickedBlock().getLocation()) == null) {
      return;
    }
    e.getPlayer().sendMessage(ChatColor.RED + "Manager: " + ChatColor.WHITE + Craftory.beltManagers
        .get(e.getClickedBlock().getLocation()));
    Craftory.beltManagers.get(e.getClickedBlock().getLocation()).getTree().getParents()
        .forEach((key, value) -> {
          e.getPlayer().sendMessage("-- KEY: " + key.toString());
        });

    e.getPlayer().sendMessage(ChatColor.RED + "Root:  " + ChatColor.WHITE + Craftory.beltManagers
        .get(e.getClickedBlock().getLocation()).getTree().getRoot().toString());
    e.getPlayer().sendMessage(ChatColor.RED + "Parents:  " + ChatColor.WHITE + Craftory.beltManagers
        .get(e.getClickedBlock().getLocation()).getTree().getParents().size());
    Craftory.beltManagers.get(e.getClickedBlock().getLocation()).getTree().print(e.getPlayer());

    //e.getPlayer().sendMessage("Location: " + e.getClickedBlock().getLocation().toString());
  }

  @EventHandler
  public void onPlayerRightClickDebugCell(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (!(ItemsAdder.matchCustomItemName(e.getItem(), "example:example_item"))) {
      return;
    }
    e.getPlayer().sendMessage("YPO");
    //if (!(Craftory.getBlockPoweredManager().isPowerStorage(e.getClickedBlock().getLocation()))) {
    //  return;
    //}
    e.getPlayer().sendMessage("Energy: " + Craftory.getBlockPoweredManager().getPoweredBlock(e.getClickedBlock().getLocation()).getEnergyStorage().getEnergyStored());
  }
}