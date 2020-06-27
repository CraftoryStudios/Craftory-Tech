package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI extends CustomBlock {

  /* Static Constants */
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected HashSet<Integer> interactableSlots = new HashSet<>();
  /* Per Object Variables */
  private Inventory inventoryInterface;

  /* Saving, Setup and Loading */
  public BlockGUI(Location location, String blockName) {
    super(location, blockName);
  }

  public BlockGUI() {
    super();
  }

  public HashSet<Integer> getInteractableSlots() {
    return interactableSlots;
  }

  @Override
  public void afterLoadUpdate() {
    setupGUI();
  }

  public abstract void setupGUI();

  /*GUI Methods */
  @Ticking(ticks = 4)
  public void updateInterface() {
    if (inventoryInterface == null || inventoryInterface.getViewers().size() <= 0) {
      return;
    }
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  public Inventory getInventory() {
    return inventoryInterface;
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    player.openInventory(inventoryInterface);
  }

  protected Inventory setInterfaceTitle(String title, String guiImage) {
    String titleBuilder = ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_16.label + guiImage
        + NegativeSpaceFont.MINUS_128.label + ChatColor.DARK_GRAY + title;
    inventoryInterface = Bukkit.createInventory(null, 54, titleBuilder);
    return inventoryInterface;
  }
}