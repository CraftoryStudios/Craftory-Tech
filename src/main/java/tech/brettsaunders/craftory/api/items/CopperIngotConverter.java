package tech.brettsaunders.craftory.api.items;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.events.Events;

public class CopperIngotConverter implements Listener {

  public CopperIngotConverter() {
    Events.registerEvents(this);
  }

  @EventHandler
  public void convertCopper(InventoryOpenEvent e) {
    for (ItemStack item : e.getPlayer().getInventory().getContents()) {
      if (item != null && item.getType() == Material.GOLD_INGOT && CustomItemManager.matchCustomItemName(item, "copper_ingot")) {
        ItemStack newItem = new ItemStack(Material.COPPER_INGOT);
        newItem.setAmount(item.getAmount());
        item.setAmount(0);
        e.getPlayer().getInventory().addItem(newItem);
      }
    }
    for (ItemStack item : e.getInventory().getContents()) {
      if (item != null && item.getType() == Material.GOLD_INGOT && CustomItemManager.matchCustomItemName(item, "copper_ingot")) {
        ItemStack newItem = new ItemStack(Material.COPPER_INGOT);
        newItem.setAmount(item.getAmount());
        item.setAmount(0);
        e.getInventory().addItem(newItem);
      }
    }
  }
}
