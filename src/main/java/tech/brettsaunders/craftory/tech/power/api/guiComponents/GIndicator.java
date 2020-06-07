package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GIndicator implements IGUIComponent {

  private final int slot;
  private final VariableContainer<Boolean> state;
  private Inventory inventory;

  public GIndicator(Inventory inventory, int slot,
      VariableContainer<Boolean> state) {
    this.inventory = inventory;
    this.slot = slot;
    this.state = state;
  }

  public GIndicator(Inventory inventory, VariableContainer<Boolean> state) {
    this.inventory = inventory;
    this.state = state;
    this.slot = 52;
  }

  @Override
  public void update() {
    if (ItemsAdder.areItemsLoaded()) {
      ItemStack light;
      String name;

      if (state.getT()) {
        light = ItemsAdder.getCustomItem("extra:light_on");
        name = "Machine Running";
      } else {
        light = ItemsAdder.getCustomItem("extra:light_off");
        name = "Machine Off";
      }
      ItemMeta meta = light.getItemMeta();
      meta.setDisplayName(name);
      light.setItemMeta(meta);
      inventory.setItem(slot, light);
    }
  }
}
