package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import java.io.Externalizable;
import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI implements Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 10009L;
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected HashSet<Integer> interactableSlots = new HashSet<>();
  /* Per Object Variables */
  private TexturedInventoryWrapper inventoryInterface;

  /* Saving, Setup and Loading */
  public BlockGUI() {
  }

  public HashSet<Integer> getInteractableSlots() {
    return interactableSlots;
  }

  public abstract void setupGUI();

  /*GUI Methods */

  public void updateInterface() {
    if (inventoryInterface == null || inventoryInterface.getInternal().getViewers().size() <= 0) {
      return;
    }
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  public Inventory getInventory() {
    if (inventoryInterface == null) {
      return null;
    }
    return inventoryInterface.getInternal();
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    inventoryInterface.showInventory(player);
  }

  protected Inventory setInterfaceTitle(String title, FontImageWrapper wrapper) {
    inventoryInterface = new TexturedInventoryWrapper(null, 54, ChatColor.DARK_GRAY + title,
        wrapper);
    return inventoryInterface.getInternal();
  }
}
