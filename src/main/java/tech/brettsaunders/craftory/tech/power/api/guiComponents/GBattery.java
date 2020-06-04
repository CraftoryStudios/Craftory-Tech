package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GBattery implements IGUIComponent {

  private static final int TOP_SLOT = 10;
  private static final int BOTTOM_SLOT = 37;
  private EnergyStorage storage;
  private Inventory inventory;
  private int previousAmount = -1;

  public GBattery(Inventory inventory, EnergyStorage storage) {
    this.inventory = inventory;
    this.storage = storage;
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
    String topTexture = "extra:bar_" + top + "_t";
    ItemStack topItem = ItemsAdder.getCustomItem(topTexture);
    ItemMeta topMeta = topItem.getItemMeta();
    topMeta.setDisplayName("Energy Stored: " + storage.getEnergyStored());
    topItem.setItemMeta(topMeta);

    //Get Bottom Battery Icon and set Display Name
    String bottomTexture = "extra:bar_" + bottom + "_b";
    ItemStack bottomItem = ItemsAdder.getCustomItem(bottomTexture);
    ItemMeta bottomMeta = bottomItem.getItemMeta();
    bottomMeta.setDisplayName("Energy Stored: " + storage.getEnergyStored());
    bottomItem.setItemMeta(bottomMeta);

    //Display in Inventory
    inventory.setItem(TOP_SLOT, topItem);
    inventory.setItem(BOTTOM_SLOT, bottomItem);
  }
}
