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

  private final int NORTH_SLOT;
  private final int EAST_SLOT;
  private final int SOUTH_SLOT;
  private final int WEST_SLOT;
  private final int UP_SLOT;
  private final int DOWN_SLOT;

  private Inventory inventory;
  private ArrayList<Boolean> config;

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config) {
    this(inventory, config, 34);
  }

  public GOutputConfig(Inventory inventory, ArrayList<Boolean> config, int middleSlot) {
    this.inventory = inventory;
    this.config = config;
    NORTH_SLOT = middleSlot - 1;
    SOUTH_SLOT = middleSlot;
    EAST_SLOT = middleSlot + 1;
    WEST_SLOT = middleSlot + 8;
    UP_SLOT = middleSlot -9;
    DOWN_SLOT = middleSlot + 9;
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
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
      config.set(1, !config.get(1));
    } else if (rawSlot == SOUTH_SLOT) {
      config.set(2, !config.get(2));
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
