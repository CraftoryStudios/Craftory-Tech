package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GBattery implements IGUIComponent {

  private EnergyStorage storage;
  private Inventory inventory;
  private static final int TOP_SLOT = 10;
  private static final int BOTTOM_SLOT = 37;
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
    double amountFilled = ((double)storage.getEnergyStored() / (double)storage.getMaxEnergyStored()) * (double)100;
    //Logger.info(amountFilled+"");
    //int changeAmount = (int) (amountFilled / 40);
    //if (changeAmount == previousAmount) return;
    //previousAmount = changeAmount;
    int bottom = 0;
    int top = 0;
    if (amountFilled != 0) {
      if (amountFilled > 50) {
        top = (int) Math.round((amountFilled - 50) / 20);
        bottom = 20;
      } else {
        bottom = (int) Math.round(amountFilled / 20);
      }
    }

    String bottomTexture = "extra:bar_"+bottom+"_b";
    String topTexture = "extra:bar_"+top+"_t";
    inventory.setItem(TOP_SLOT, ItemsAdder.getCustomItem(topTexture));
    inventory.setItem(BOTTOM_SLOT, ItemsAdder.getCustomItem(bottomTexture));
  }
}
