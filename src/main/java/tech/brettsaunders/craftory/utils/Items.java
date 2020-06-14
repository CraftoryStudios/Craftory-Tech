package tech.brettsaunders.craftory.utils;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.ItemStack;

public class Items {

  public static String getItemName(ItemStack itemStack) {
    if (ItemsAdder.isCustomItem(itemStack)) {
      return ItemsAdder.getCustomItemName(itemStack);
    } else {
      return itemStack.getType().toString();
    }
  }

  public class Power {

    public static final String WRENCH = "craftory:wrench";
    public static final String CONFIGURATOR = "craftory:configurator";
  }

  public class Components {

    public static final String STEEL_INGOT = "craftory:steel_ingot";
    public static final String EMERALD_CORE = "craftory:emerald_core";
    public static final String DIAMOND_CORE = "craftory:diamond_core";
    public static final String GOLD_CORE = "craftory:gold_core";
    public static final String IRON_CORE = "craftory:iron_core";
  }

}
