package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GOneToOneMachine implements IGUIComponent, Listener {

  private final int slot;
  private final VariableContainer<Double> progress;
  private final Inventory inventory;
  private final int inputSlot;
  private final int outputSlot;
  private static final HashSet<InventoryAction> outputDisabledActions = new HashSet<>(Arrays.asList(InventoryAction.SWAP_WITH_CURSOR, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME));

  public GOneToOneMachine(Inventory inventory, int slot,
      VariableContainer<Double> progress, int inputSlot, int outputSlot) {
    this.inventory = inventory;
    this.slot = slot;
    this.progress = progress;
    this.inputSlot = inputSlot;
    this.outputSlot = outputSlot;
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
  }

  public GOneToOneMachine(Inventory inventory,
      VariableContainer<Double> progress, int inputSlot, int outputSlot) {
    this.inventory = inventory;
    this.progress = progress;
    this.outputSlot = outputSlot;
    this.inputSlot = inputSlot;
    this.slot = 24;
  }

  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != inventory) {
      return;
    }

    //Stop moving items from any slot put intractable
    if (event.getRawSlot() != inputSlot && event.getRawSlot() != outputSlot && event.getRawSlot() < 54) {
      event.setCancelled(true);
    }

    //TODO Need to add shift click
    //Stop inserting items into output slot
    if (event.getRawSlot() == outputSlot) {
      if (outputDisabledActions.contains(event.getAction())) {
        event.setCancelled(true);
      }
    }

  }

  @Override
  public void update() {
    if (ItemsAdder.areItemsLoaded()) {
      int x = (int) Math.floor(progress.getT() * 10);
      ItemStack arrow = ItemsAdder.getCustomItem("extra:arrow_" + x);
      ItemMeta meta = arrow.getItemMeta();
      meta.setDisplayName("");
      arrow.setItemMeta(meta);
      inventory.setItem(slot, arrow);
    }
  }
}
