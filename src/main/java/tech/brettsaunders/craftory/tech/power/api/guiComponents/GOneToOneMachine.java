package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GOneToOneMachine implements IGUIComponent {

  private final int slot;
  private final VariableContainer<Double> progress;
  private final Inventory inventory;

  public GOneToOneMachine(Inventory inventory, int slot,
      VariableContainer<Double> progress) {
    this.inventory = inventory;
    this.slot = slot;
    this.progress = progress;
  }

  public GOneToOneMachine(Inventory inventory,
      VariableContainer<Double> progress) {
    this.inventory = inventory;
    this.progress = progress;
    this.slot = 24;
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
