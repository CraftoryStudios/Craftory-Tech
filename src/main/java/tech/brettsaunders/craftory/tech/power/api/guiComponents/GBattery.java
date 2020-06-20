package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GBattery implements IGUIComponent {

  private final int TOP_SLOT;
  private final int BOTTOM_SLOT;
  private final EnergyStorage storage;
  private final Inventory inventory;

  public GBattery(Inventory inventory, EnergyStorage storage, int top_slot) {
    this.inventory = inventory;
    this.storage = storage;
    TOP_SLOT = top_slot;
    BOTTOM_SLOT = top_slot + 27;
  }

  public GBattery(Inventory inventory, EnergyStorage storage) {
    this(inventory, storage, 10);
  }

  @Override
  public void update() {
    setLevelIndicator();
  }

  private void setLevelIndicator() {
    //Percentage of capacity filled
    double amountFilled =
        ((double) storage.getEnergyStored() / (double) storage.getMaxEnergyStored()) * (double) 100;

    //Calculate amount of power bars to display
    int bottom = 0;
    int top = 0;
    if (amountFilled != 0) {
      if (amountFilled > 50) {
        top = (int) Math.round((amountFilled - 50) * 0.4);
        bottom = 20;
      } else {
        bottom = (int) Math.round(amountFilled * 0.4);
      }
    }

    //Get Top Battery Icon and set Display Name
    String topTexture = "bar_" + top + "_t";
    ItemStack topItem = CustomItemManager.getCustomItem(topTexture);
    ItemMeta topMeta = topItem.getItemMeta();
    topMeta.setDisplayName("Energy Stored: " + storage.getEnergyStored());
    topItem.setItemMeta(topMeta);

    //Get Bottom Battery Icon and set Display Name
    String bottomTexture = "bar_" + bottom + "_b";
    ItemStack bottomItem = CustomItemManager.getCustomItem(bottomTexture);
    ItemMeta bottomMeta = bottomItem.getItemMeta();
    bottomMeta.setDisplayName("Energy Stored: " + storage.getEnergyStored());
    bottomItem.setItemMeta(bottomMeta);

    //Fill other battery slots
    ItemStack batteryIndicator = CustomItemManager.getCustomItem("invisible");
    ItemMeta batteryIndicatorMeta = batteryIndicator.getItemMeta();
    batteryIndicatorMeta.setDisplayName("Energy Stored: " + storage.getEnergyStored());
    batteryIndicator.setItemMeta(batteryIndicatorMeta);

    //Display in Inventory
    inventory.setItem(TOP_SLOT, topItem);
    inventory.setItem(BOTTOM_SLOT, bottomItem);

    //Fill other slots
    for (int i = -1; i < 1; i++) {
      int x = TOP_SLOT + i;
      for (int j = -1; j < 5; j++) {
        int slot = x + (9 * j);
        if (slot > -1 && slot < 54 && slot != TOP_SLOT && slot != BOTTOM_SLOT) {
          inventory.setItem(slot, batteryIndicator);
        }
      }
    }
  }
}
