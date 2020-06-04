package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.Logger;

public abstract class BlockGUI implements Externalizable {


  /* Static Constants */
  private static final long serialVersionUID = 10009L;

  /* Per Object Variables */
  private TexturedInventoryWrapper inventoryInterface;
  private String title;
  private String backgroundImage;

  private ArrayList<IGUIComponent> components = new ArrayList<>();

  /* Saving, Setup and Loading */
  public BlockGUI() {
    updateGUI();
    title = "";
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(title);
    out.writeUTF(backgroundImage);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    title = in.readUTF();
    backgroundImage = in.readUTF();
  }

  /*GUI Methods */
  protected void setGUITitle(String title) {
    this.title = title;
    updateGUI();
  }

  protected void setGUIBackgroundImage(String imageName) {
    this.backgroundImage = imageName;
    updateGUI();
  }

  protected void setGUIDetails(String title, String backgroundImageName) {
    this.backgroundImage = backgroundImageName;
    this.title = title;
    updateGUI();
  }

  private void updateGUI() {
//    if (title != null && !backgroundImage.isEmpty()) {
//      inventoryInterface = new TexturedInventoryWrapper(null, 54, title, new FontImageWrapper(backgroundImage));
//    } else {
//
//    }
    title = ChatColor.DARK_GRAY+ "Cell";

    inventoryInterface = new TexturedInventoryWrapper(null, 54, title, new FontImageWrapper("extra:cell"));
  }

  public void updateInterface() {
    for  (IGUIComponent component: components) {
      component.update();
    }
  }

   public void addGUIComponent(IGUIComponent component) {
    components.add(component);
     Logger.info("Added Component");
   }

   public Inventory getInventory() {
    return inventoryInterface.getInternal();
   }

  public void openGUI(Player player) {
    inventoryInterface.showInventory(player);
  }
}
