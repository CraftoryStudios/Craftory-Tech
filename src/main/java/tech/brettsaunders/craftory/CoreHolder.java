package tech.brettsaunders.craftory;

import org.bukkit.block.BlockFace;

public class CoreHolder {

  public static final int TICK = 1;
  public static final int FOUR_TICKS = 4;

  /* Block Names */
  public static class Blocks {

    /* Power */
    public static final String IRON_CELL = "iron_cell";
    public static final String GOLD_CELL = "gold_cell";
    public static final String DIAMOND_CELL = "diamond_cell";
    public static final String EMERALD_CELL = "emerald_cell";
    public static final String SOLID_FUEL_GENERATOR = "solid_fuel_generator";
    public static final String POWER_CONNECTOR = "power_connector";
    public static final String IRON_ELECTRIC_FURNACE = "iron_electric_furnace";
    public static final String GOLD_ELECTRIC_FURNACE = "gold_electric_furnace";
    public static final String DIAMOND_ELECTRIC_FURNACE = "diamond_electric_furnace";
    public static final String EMERALD_ELECTRIC_FURNACE = "emerald_electric_furnace";
    public static final String IRON_FOUNDRY = "iron_foundry";
  }

  /* Item Names */
  public static class Items {

    /* Power */
    public static final String WRENCH = "wrench";
    public static final String CONFIGURATOR = "configurator";

    /* Components */
    public static final String STEEL_INGOT = "steel_ingot";
    public static final String EMERALD_CORE = "emerald_core";
    public static final String DIAMOND_CORE = "diamond_core";
    public static final String GOLD_CORE = "gold_core";
    public static final String IRON_CORE = "iron_core";
  }

  public enum INTERACTABLEBLOCK {
    NONE,
    RECIEVER,
    HOPPER_IN,
    HOPPER_OUT;
  }

  public static final BlockFace[] HOPPER_INTERACT_FACES = {BlockFace.NORTH, BlockFace.EAST,
      BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
}
