package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GOutputConfig implements IGUIComponent, Listener {

  private static final int NORTH_SLOT = 32;
  private static final int EAST_SLOT = 33;
  private static final int SOUTH_SLOT = 34;
  private static final int WEST_SLOT = 41;
  private static final int UP_SLOT = 24;
  private static final int DOWN_SLOT = 42;

  private Inventory inventory;
  private ArrayList<Boolean> config;

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config) {
    this.inventory = inventory;
    this.config = config;
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
  }

  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != inventory) {
      return;
    }

    //Stop moving items from any slot put intractable
    if (event.getRawSlot() != 12 && event.getRawSlot() < 54) {
      event.setCancelled(true); //TODO Move so don't have to use 22
    }

    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    switch (event.getRawSlot()) {
      case NORTH_SLOT:
        config.set(0, !config.get(0));
        break;
      case EAST_SLOT:
        config.set(1, !config.get(1));
        break;
      case SOUTH_SLOT:
        config.set(2, !config.get(2));
        break;
      case WEST_SLOT:
        config.set(3, !config.get(3));
        break;
      case UP_SLOT:
        config.set(4, !config.get(4));
        break;
      case DOWN_SLOT:
        config.set(5, !config.get(5));
        break;

    }
  }

  @Override
  public void update() {
    if (ItemsAdder.areItemsLoaded()) {
      final ItemStack DISABLED = ItemsAdder.getCustomItem("extra:output_disabled");
      final ItemStack OUTPUT = ItemsAdder.getCustomItem("extra:output_green");

      //NORTH, EAST, SOUTH, WEST, UP, DOWN
      inventory.setItem(NORTH_SLOT, config.get(0) == false ? DISABLED.clone() : OUTPUT.clone());
      inventory.setItem(EAST_SLOT, config.get(1) == false ? DISABLED.clone() : OUTPUT.clone());
      inventory.setItem(SOUTH_SLOT, config.get(2) == false ? DISABLED.clone() : OUTPUT.clone());
      inventory.setItem(WEST_SLOT, config.get(3) == false ? DISABLED.clone() : OUTPUT.clone());
      inventory.setItem(UP_SLOT, config.get(4) == false ? DISABLED.clone() : OUTPUT.clone());
      inventory.setItem(DOWN_SLOT, config.get(5) == false ? DISABLED.clone() : OUTPUT.clone());
    }
  }
}
