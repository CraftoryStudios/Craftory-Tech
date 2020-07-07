package tech.brettsaunders.craftory;

import org.bukkit.block.BlockFace;

public class CoreHolder {

  public static final BlockFace[] HOPPER_INTERACT_FACES = {BlockFace.NORTH, BlockFace.EAST,
      BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

  public enum INTERACTABLEBLOCK {
    NONE,
    RECEIVER,
    HOPPER_IN,
    HOPPER_OUT
  }

  /* Block Names */
  public static class Blocks {

    /* Power */
    public static final String IRON_CELL = "IronCell";
    public static final String GOLD_CELL = "GoldCell";
    public static final String DIAMOND_CELL = "DiamondCell";
    public static final String EMERALD_CELL = "EmeraldCell";
    public static final String SOLID_FUEL_GENERATOR = "SolidFuelGenerator";
    public static final String POWER_CONNECTOR = "power_connector";
    public static final String IRON_ELECTRIC_FURNACE = "IronElectricFurnace";
    public static final String GOLD_ELECTRIC_FURNACE = "GoldElectricFurnace";
    public static final String DIAMOND_ELECTRIC_FURNACE = "DiamondElectricFurnace";
    public static final String EMERALD_ELECTRIC_FURNACE = "EmeraldElectricFurnace";
    public static final String IRON_ELECTRIC_FOUNDRY = "IronElectricFoundry";
    public static final String GOLD_ELECTRIC_FOUNDRY = "GoldElectricFoundry";
    public static final String DIAMOND_ELECTRIC_FOUNDRY = "DiamondElectricFoundry";
    public static final String EMERALD_ELECTRIC_FOUNDRY = "EmeraldElectricFoundry";
    public static final String IRON_FOUNDRY = "IronFoundry";
    public static final String ARROW_TURRET = "ArrowTurret";

    /* Ores */
    public static final String COPPER_ORE = "CopperOre";
    public static final String CRYSTAL_ORE = "crystal";
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
    public static final String MAGNETISED_IRON = "magnetised_iron";

    public static final String ARROW_TURRET_HEAD = "ArrowTurretHead";
  }
}
