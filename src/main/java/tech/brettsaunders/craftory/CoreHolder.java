package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.ItemStack;

public class CoreHolder {
  public static final int TICK = 1;
  public static final int FOUR_TICKS = 4;

  public static String getItemName(ItemStack itemStack) {
    if (ItemsAdder.isCustomItem(itemStack)) {
      return ItemsAdder.getCustomItemName(itemStack);
    } else {
      return itemStack.getType().toString();
    }
  }


  /* Block Names */
  public static class Blocks {

    /* Power */
    public static final String IRON_CELL = "craftory:iron_cell";
    public static final String GOLD_CELL = "craftory:gold_cell";
    public static final String DIAMOND_CELL = "craftory:diamond_cell";
    public static final String EMERALD_CELL = "craftory:emerald_cell";
    public static final String SOLID_FUEL_GENERATOR = "craftory:solid_fuel_generator";
    public static final String POWER_CONNECTOR = "craftory:power_connector";
    public static final String IRON_ELECTRIC_FURNACE = "craftory:iron_electric_furnace";
    public static final String GOLD_ELECTRIC_FURNACE = "craftory:gold_electric_furnace";
    public static final String DIAMOND_ELECTRIC_FURNACE = "craftory:diamond_electric_furnace";
    public static final String EMERALD_ELECTRIC_FURNACE = "craftory:emerald_electric_furnace";
    public static final String IRON_FOUNDRY = "craftory:iron_foundry";
  }

  /* Item Names */
  public static class Items {

    /* Power */
    public static final String WRENCH = "craftory:wrench";
    public static final String CONFIGURATOR = "craftory:configurator";

    /* Components */
    public static final String STEEL_INGOT = "craftory:steel_ingot";
    public static final String EMERALD_CORE = "craftory:emerald_core";
    public static final String DIAMOND_CORE = "craftory:diamond_core";
    public static final String GOLD_CORE = "craftory:gold_core";
    public static final String IRON_CORE = "craftory:iron_core";
  }
}
