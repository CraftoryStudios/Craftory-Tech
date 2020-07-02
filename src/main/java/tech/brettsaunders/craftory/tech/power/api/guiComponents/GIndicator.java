package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class GIndicator implements IGUIComponent {

  private final int slot;
  private final VariableContainer<Boolean> state;
  private final Inventory inventory;

  public GIndicator(Inventory inventory, VariableContainer<Boolean> state) {
    this(inventory, state, 52);
  }

  public GIndicator(Inventory inventory, VariableContainer<Boolean> state, int slot) {
    this.inventory = inventory;
    this.state = state;
    this.slot = slot;
  }

  @Override
  public void update() {
    ItemStack light;
    String name;

    if (state.getT()) {
      light = CustomItemManager.getCustomItem("light_on");
      name = "Machine Running";
    } else {
      light = CustomItemManager.getCustomItem("light_off");
      name = "Machine Off";
    }
    ItemMeta meta = light.getItemMeta();
    meta.setDisplayName(ChatColor.RESET +name);
    light.setItemMeta(meta);
    inventory.setItem(slot, light);
  }
}
