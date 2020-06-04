package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.Logger;

public abstract class BlockGUI implements Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 10009L;

  /* Per Object Variables */
  private TexturedInventoryWrapper inventoryInterface;

  private ArrayList<IGUIComponent> components = new ArrayList<>();

  /* Saving, Setup and Loading */
  public BlockGUI() {
        updateGUI(); //TODO THE ISSUE
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
  }

  /*GUI Methods */

  private void updateGUI() {
//    if (title != null && !backgroundImage.isEmpty()) {
//      inventoryInterface = new TexturedInventoryWrapper(null, 54, title, new FontImageWrapper(backgroundImage));
//    } else {
//
//    }
    String title = ChatColor.DARK_GRAY + "Cell";

    inventoryInterface = new TexturedInventoryWrapper(null, 54, title,
        new FontImageWrapper("extra:cell"));
  }

  public void updateInterface() {
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public Inventory getInventory() {
    return inventoryInterface.getInternal();
  }

  public void openGUI(Player player) {
    inventoryInterface.showInventory(player);
  }
}
