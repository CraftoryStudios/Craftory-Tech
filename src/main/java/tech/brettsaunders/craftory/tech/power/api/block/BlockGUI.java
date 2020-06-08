package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI implements Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 10009L;

  /* Per Object Variables */
  private TexturedInventoryWrapper inventoryInterface;
  private final ArrayList<IGUIComponent> components = new ArrayList<>();

  protected HashSet<Integer> interactableSlots = new HashSet<>();

  public HashSet<Integer> getInteractableSlots() {
    return interactableSlots;
  }


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

  public Inventory getInventory() {
    return inventoryInterface.getInternal();
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

  protected void fillBlankSlots(HashSet<Integer> protectedSlots) {
    int inventorySize = inventoryInterface.getInternal().getSize();
    ItemStack invisibleItem = ItemsAdder.getCustomItem("extra:invisible");
    ItemMeta invisibleItemMeta = invisibleItem.getItemMeta();
    invisibleItemMeta.setDisplayName("");
    invisibleItem.setItemMeta(invisibleItemMeta);

    for (int i = 0; i < inventorySize; i++) {
      if (!(protectedSlots.contains(i)) && inventoryInterface.getInternal().getItem(i) == null) {
        inventoryInterface.getInternal().setItem(i, invisibleItem.clone());
      }
    }
  }
}
