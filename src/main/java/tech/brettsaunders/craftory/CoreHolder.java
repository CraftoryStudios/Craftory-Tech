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
    public static final String IRON_FOUNDRY = "IronMacerator";
    public static final String IRON_MACERATOR = "IronMacerator";
    public static final String GOLD_MACERATOR = "GoldMacerator";
    public static final String DIAMOND_MACERATOR = "DiamondMacerator";
    public static final String EMERALD_MACERATOR = "EmeraldMacerator";
    public static final String TURRET_PLATFORM = "TurretPlatform";

    public static final String BASIC_SOLAR_PANEL = "BasicSolarPanel";
    public static final String SOLAR_PANEL = "SolarPanel";
    public static final String COMPACTED_SOLAR_PANEL = "CompactedSolarPanel";
    public static final String SOLAR_ARRAY = "SolarArray";
    public static final String GEOTHERMAL_GENERATOR = "GeothermalGenerator";
    public static final String WIND_GENERATOR = "WindGenerator";
    public static final String MAGNETISER = "Magnetiser";
    public static final String MAGNETISING_TABLE = "MagnetisingTable";

    public static final String BLOCK_BREAKER = "BlockBreaker";
    /* Ores */
    public static final String COPPER_ORE = "CopperOre";
    public static final String CRYSTAL_ORE = "crystal";
  }

  /* Item Names */
  public static class Items {

    /* Power */
    public static final String WRENCH = "wrench";
    public static final String CONFIGURATOR = "configurator";
    public static final String ENGINEERS_HAMMER = "engineers_hammer";
    /* Components */
    public static final String STEEL_INGOT = "steel_ingot";
    public static final String EMERALD_CORE = "emerald_core";
    public static final String DIAMOND_CORE = "diamond_core";
    public static final String GOLD_CORE = "gold_core";
    public static final String IRON_CORE = "iron_core";
    public static final String MAGNETISED_IRON = "magnetised_iron";
    public static final String COAL_DUST = "coal_dust";
    public static final String COPPER_DUST = "copper_dust";
    public static final String STEEL_DUST = "steel_dust";
    public static final String IRON_DUST = "iron_dust";
    public static final String GOLD_DUST = "gold_dust";
    public static final String DIAMOND_DUST = "diamond_dust";
    public static final String EMERALD_DUST = "emerald_dust";


    public static final String BASE_TURRET = "base_turret";
    public static final String GUN_TURRET = "gun_turret";
    public static final String UPGRADE_CARD_EMERALD = "upgrade_card_emerald";
    public static final String UPGRADE_CARD_DIAMOND = "upgrade_card_diamond";
    public static final String UPGRADE_CARD_GOLD = "upgrade_card_gold";
    public static final String UPGRADE_CARD_IRON = "upgrade_card_iron";

    /* Tools */
    public static final String SICKLE_WOOD = "sickle_wood";
    public static final String SICKLE_STONE = "sickle_stone";
    public static final String SICKLE_IRON = "sickle_iron";
    public static final String SICKLE_GOLD = "sickle_gold";
    public static final String SICKLE_STEEL = "sickle_steel";
    public static final String SICKLE_COPPER = "sickle_copper";
    public static final String SICKLE_DIAMOND = "sickle_diamond";

    /* World Items */
    public static final String WINDMILL = "windmill";
  }

  public static enum FLUIDS {
    LAVA ("lava"),
    WATER ("water");

    private final String name;

    private FLUIDS(String s) {
      name = s;
    }

    public String toString() {
      return this.name;
    }
  }
}
