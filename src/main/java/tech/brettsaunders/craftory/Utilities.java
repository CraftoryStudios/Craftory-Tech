package tech.brettsaunders.craftory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.logging.Level;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlockFactory;
import tech.brettsaunders.craftory.api.blocks.basicBlocks.CopperOre;
import tech.brettsaunders.craftory.commands.CommandWrapper;
import tech.brettsaunders.craftory.tech.power.core.block.cell.DiamondCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.EmeraldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.GoldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.IronCell;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.DiamondElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.EmeraldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.GoldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.IronElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.IronFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelGenerator;

public class Utilities {

  public static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  public final static String DATA_FOLDER;
  public static FileConfiguration config;
  public static FileConfiguration data;
  private static File configFile = new File(Craftory.plugin.getDataFolder(), "config.yml");
  private static File dataFile = new File(Craftory.plugin.getDataFolder(), "data.yml");
  private static String UNIT = "Re";
  private static DecimalFormat df = new DecimalFormat("###.###");

  static {
    config = YamlConfiguration
        .loadConfiguration(new File(Craftory.plugin.getDataFolder(), "config.yml"));
    data = YamlConfiguration
        .loadConfiguration(new File(Craftory.plugin.getDataFolder(), "data.yml"));
    DATA_FOLDER = Craftory.plugin.getDataFolder().getPath() + File.separator + "data";
  }

  static void pluginBanner() {
    drawBanner("&2-------------------------------------------");
    drawBanner("&2   _____            __ _                   ");
    drawBanner("&2  / ____|          / _| |                  ");
    drawBanner("&2 | |     _ __ __ _| |_| |_ ___  _ __ _   _ ");
    drawBanner("&2 | |    | '__/ _` |  _| __/ _ \\| '__| | | |");
    drawBanner("&2 | |____| | | (_| | | | || (_) | |  | |_| |");
    drawBanner("&2  \\_____|_|  \\__,_|_|  \\__\\___/|_|   \\__, |");
    drawBanner("&2                                      __/ |");
    drawBanner("&2                                     |___/ ");
    drawBanner("&2-------------------------------------------");
  }

  static void createConfigs() {
    config.options().header("Craftory");
    config.addDefault("general.debug", false);
    config.addDefault("general.techEnabled", true);
    config.options().copyHeader(true);
    config.options().copyDefaults(true);
    saveConfigFile();
    reloadConfigFile();

    data.options().header("Do Not Touch");
    data.addDefault("reporting.serverUUID", UUID.randomUUID().toString());
    data.options().copyHeader(true);
    data.options().copyDefaults(true);
    saveDataFile();
    reloadDataFile();
  }

  static void createDataPath() {
    File file = new File(DATA_FOLDER);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  static void startMetrics() {
    Metrics metrics = new Metrics(Craftory.plugin, 7804);
    metrics.addCustomChart(
        new Metrics.SimplePie("debugEnabled", () -> config.getString("general.debug")));
    metrics.addCustomChart(
        new Metrics.SimplePie("techEnabled", () -> config.getString("general.techEnabled")));
  }

  static void registerCommandsAndCompletions() {
    Craftory.plugin.getCommand("craftory").setExecutor(new CommandWrapper());
    Craftory.plugin.getCommand("cr").setExecutor(new CommandWrapper());
    Craftory.plugin.getCommand("craftory").setTabCompleter(new CommandWrapper());
    Craftory.plugin.getCommand("cr").setTabCompleter(new CommandWrapper());
  }

  static void registerEvents() {

  }

  static void registerCustomBlocks() {
    CustomBlockFactory customBlockFactory = Craftory.customBlockFactory;
    customBlockFactory.registerCustomBlock(Blocks.IRON_CELL, IronCell.class);
    customBlockFactory.registerCustomBlock(Blocks.GOLD_CELL, GoldCell.class);
    customBlockFactory.registerCustomBlock(Blocks.DIAMOND_CELL, DiamondCell.class);
    customBlockFactory.registerCustomBlock(Blocks.EMERALD_CELL, EmeraldCell.class);
    customBlockFactory.registerCustomBlock(Blocks.IRON_ELECTRIC_FURNACE, IronElectricFurnace.class);
    customBlockFactory.registerCustomBlock(Blocks.GOLD_ELECTRIC_FURNACE, GoldElectricFurnace.class);
    customBlockFactory.registerCustomBlock(Blocks.DIAMOND_ELECTRIC_FURNACE, DiamondElectricFurnace.class);
    customBlockFactory.registerCustomBlock(Blocks.EMERALD_ELECTRIC_FURNACE, EmeraldElectricFurnace.class);
    customBlockFactory.registerCustomBlock(Blocks.IRON_FOUNDRY, IronFoundry.class);
    customBlockFactory.registerCustomBlock(Blocks.SOLID_FUEL_GENERATOR, SolidFuelGenerator.class);
    customBlockFactory.registerCustomBlock(Blocks.COPPER_ORE, CopperOre.class);
  }

  static void registerBlocks() {
    Craftory.tickManager.registerCustomBlockClass(DiamondCell.class);
    Craftory.tickManager.registerCustomBlockClass(EmeraldCell.class);
    Craftory.tickManager.registerCustomBlockClass(GoldCell.class);
    Craftory.tickManager.registerCustomBlockClass(IronCell.class);
    Craftory.tickManager.registerCustomBlockClass(DiamondElectricFurnace.class);
    Craftory.tickManager.registerCustomBlockClass(EmeraldElectricFurnace.class);
    Craftory.tickManager.registerCustomBlockClass(GoldElectricFurnace.class);
    Craftory.tickManager.registerCustomBlockClass(IronElectricFurnace.class);
    Craftory.tickManager.registerCustomBlockClass(IronFoundry.class);
    Craftory.tickManager.registerCustomBlockClass(SolidFuelGenerator.class);
  }

  static void done() {
    Bukkit.getLogger().info(
        "[" + Craftory.plugin.getDescription().getPrefix() + "] " + ChatColor.GREEN
            + "Finished Loading!");
  }

  /* Helper Functions */
  public static void reloadConfigFile() {
    if (configFile == null) {
      configFile = new File(Craftory.plugin.getDataFolder(), "config.yml");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public static void reloadDataFile() {
    if (dataFile == null) {
      dataFile = new File(Craftory.plugin.getDataFolder(), "data.yml");
    }
    data = YamlConfiguration.loadConfiguration(dataFile);
  }

  public static void saveDataFile() {
    if (data == null || dataFile == null) {
      return;
    }
    try {
      data.save(dataFile);
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, "Could not save " + dataFile, ex);
    }
  }

  public static void saveConfigFile() {
    if (config == null || configFile == null) {
      return;
    }
    try {
      config.save(configFile);
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
    }
  }

  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  public static void msg(final CommandSender s, String msg) {
    if (s instanceof Player) {
      msg = ChatColor.translateAlternateColorCodes('&', msg);
    } else {
      msg = ChatColor.translateAlternateColorCodes('&', "[Craftory]" + msg);
    }
    s.sendMessage(msg);
  }

  private static void drawBanner(final String message) {
    Bukkit.getServer().getConsoleSender().sendMessage(
        ChatColor.translateAlternateColorCodes('&', message));
  }

  public static String getRegionID(Chunk chunk) {
    int regionX = chunk.getX() >> 5;
    int regionZ = chunk.getZ() >> 5;
    return "r." + regionX + "," + regionZ + ".nbt";
  }

  public static String getChunkID(Chunk chunk) {
    return chunk.getX() + "," + chunk.getZ();
  }

  public static String getLocationID(Location location) {
    return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
  }

  public static String rawToPrefixed(Integer energy) {
    String s = Integer.toString(energy);
    int length = s.length();
    //if(length < 3) return s + " " + UNIT;
    if (length < 7) {
      return s + " " + UNIT;
    }
    //if(length < 7) return df.format(energy/1000f) +" K" + UNIT;
    if (length < 10) {
      return df.format(energy / 1000000f) + " M" + UNIT;
    }
    if (length < 13) {
      return df.format(energy / 1000000000f) + " G" + UNIT;
    }
    if (length < 16) {
      return df.format(energy / 1000000000000f) + " T" + UNIT;
    }
    if (length < 19) {
      return df.format(energy / 1000000000000000f) + " P" + UNIT;
    }
    if (length < 22) {
      return df.format(energy / 1000000000000000000f) + " E" + UNIT;
    }
    return "A bukkit load";
  }

}
