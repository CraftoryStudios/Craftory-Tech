package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI extends CustomBlock implements Listener {

  /* Static Constants */
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected HashSet<Integer> interactableSlots = new HashSet<>();

  private static final HashSet<InventoryAction> outputDisabledActions = new HashSet<>(Arrays
      .asList(InventoryAction.SWAP_WITH_CURSOR, InventoryAction.PLACE_ALL,
          InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME));
  /* Per Object Variables */
  private Inventory inventoryInterface;

  /* Saving, Setup and Loading */
  public BlockGUI(Location location, String blockName) { super(location, blockName); }

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
    String titleSpaced = title.replaceAll("(.)([A-Z])", "$1 $2");
    String titleBuilder = ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_16.label + guiImage
        + NegativeSpaceFont.MINUS_128.label + NegativeSpaceFont.MINUS_16.label + negativeSpaceGenerator((int)Math.floor(titleSpaced.length()/2)) + ChatColor.DARK_GRAY + titleSpaced;
    inventoryInterface = Bukkit.createInventory(null, 54, titleBuilder);
    return inventoryInterface;
  }

  //TODO Could cache
  private String negativeSpaceGenerator(int size) {
    String result = "";
    String[] conversion = (String.format("%10s", Integer.toBinaryString(size)).replace(' ', '0')).split("");
    result += conversion[0].equals("1") ? NegativeSpaceFont.MINUS_32.label : "";
    result += conversion[1].equals("1") ? NegativeSpaceFont.MINUS_16.label : "";
    result += conversion[2].equals("1") ? NegativeSpaceFont.MINUS_8.label : "";
    result += conversion[3].equals("1") ? NegativeSpaceFont.MINUS_7.label : "";
    result += conversion[4].equals("1") ? NegativeSpaceFont.MINUS_6.label : "";
    result += conversion[5].equals("1") ? NegativeSpaceFont.MINUS_5.label : "";
    result += conversion[6].equals("1") ? NegativeSpaceFont.MINUS_4.label : "";
    result += conversion[7].equals("1") ? NegativeSpaceFont.MINUS_3.label : "";
    result += conversion[8].equals("1") ? NegativeSpaceFont.MINUS_2.label : "";
    result += conversion[9].equals("1") ? NegativeSpaceFont.MINUS_1.label : "";
    return result;
  }
}