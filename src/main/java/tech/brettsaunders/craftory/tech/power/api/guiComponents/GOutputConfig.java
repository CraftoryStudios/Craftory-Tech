package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GOutputConfig implements IGUIComponent, Listener {

  private final int NORTH_SLOT;
  private final int EAST_SLOT;
  private final int SOUTH_SLOT;
  private final int WEST_SLOT;
  private final int UP_SLOT;
  private final int DOWN_SLOT;
  private final boolean ALT;

  private final Inventory inventory;
  private final ArrayList<Boolean> config;

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config) {
    this(inventory, config, 34);
  }

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config, int middleSlot) {
    this(inventory, config, middleSlot, false);
  }

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config, int middleSlot,
      boolean alt) {
    this.inventory = inventory;
    this.config = config;
    SOUTH_SLOT = middleSlot - 1;
    UP_SLOT = middleSlot - 9;
    DOWN_SLOT = middleSlot + 9;
    EAST_SLOT = middleSlot + 8;
    WEST_SLOT = middleSlot + 1;
    NORTH_SLOT = middleSlot;
    ALT = alt;
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != inventory) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    int rawSlot = event.getRawSlot();
    if (rawSlot == NORTH_SLOT) {
      config.set(0, !config.get(0));
    } else if (rawSlot == EAST_SLOT) {
      config.set(2, !config.get(2));
    } else if (rawSlot == SOUTH_SLOT) {
      config.set(1, !config.get(1));
    } else if (rawSlot == WEST_SLOT) {
      config.set(3, !config.get(3));
    } else if (rawSlot == UP_SLOT) {
      config.set(4, !config.get(4));
    } else if (rawSlot == DOWN_SLOT) {
      config.set(5, !config.get(5));
    }
  }

  @Override
  public void update() {
    final ItemStack DISABLED = CustomItemManager
        .getCustomItem("output_disabled" + (ALT ? "_alt" : ""));
    final ItemStack OUTPUT = CustomItemManager
        .getCustomItem("output_green" + (ALT ? "_alt" : ""));

    //NORTH, EAST, SOUTH, WEST, UP, DOWN
    inventory.setItem(NORTH_SLOT, !config.get(0) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(SOUTH_SLOT, !config.get(1) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(EAST_SLOT, !config.get(2) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(WEST_SLOT, !config.get(3) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(UP_SLOT, !config.get(4) ? DISABLED.clone() : OUTPUT.clone());
    inventory.setItem(DOWN_SLOT, !config.get(5) ? DISABLED.clone() : OUTPUT.clone());
  }
}
