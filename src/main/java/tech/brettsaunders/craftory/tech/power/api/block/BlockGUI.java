package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI implements Externalizable {

  /* Static Constants */
  private static final long serialVersionUID = 10009L;
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected HashSet<Integer> interactableSlots = new HashSet<>();
  /* Per Object Variables */
  private Inventory inventoryInterface;

  /* Saving, Setup and Loading */
  public BlockGUI() {
  }

  public HashSet<Integer> getInteractableSlots() {
    return interactableSlots;
  }

  public abstract void setupGUI();

  /*GUI Methods */

  public void updateInterface() {
    if (inventoryInterface == null || inventoryInterface.getViewers().size() <= 0) {
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
    return inventoryInterface;
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    player.openInventory(inventoryInterface);
  }

  protected Inventory setInterfaceTitle(String title, String guiImage) {
    String titleBuilder = ChatColor.WHITE +"" + NegativeSpaceFont.MINUS_16.label + guiImage + NegativeSpaceFont.MINUS_128.label + ChatColor.DARK_GRAY + title;
    inventoryInterface = Bukkit.createInventory(null, 54, titleBuilder);
    return inventoryInterface;
  }
}