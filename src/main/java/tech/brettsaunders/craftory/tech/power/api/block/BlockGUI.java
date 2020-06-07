package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI implements Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 10009L;

  /* Per Object Variables */
  private TexturedInventoryWrapper inventoryInterface;
  private ArrayList<IGUIComponent> components = new ArrayList<>();

  /* Saving, Setup and Loading */
  public BlockGUI() {
  }

  public abstract void setupGUI();

  /*GUI Methods */

  public void updateInterface() {
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    inventoryInterface.showInventory(player);
  }

  protected Inventory setInterfaceTitle(String title, FontImageWrapper wrapper) {
    inventoryInterface = new TexturedInventoryWrapper(null, 54, title,
        wrapper);
    return inventoryInterface.getInternal();
  }
}
