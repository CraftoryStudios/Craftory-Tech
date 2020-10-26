/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.UserBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Synchronized;
import org.bstats.bukkit.Metrics;
import org.bstats.bukkit.Metrics.AdvancedPie;
import org.bstats.bukkit.Metrics.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockFactory;
import tech.brettsaunders.craftory.api.blocks.BasicBlocks;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.commands.CommandWrapper;
import tech.brettsaunders.craftory.tech.power.core.block.cell.DiamondCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.EmeraldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.GoldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.IronCell;
import tech.brettsaunders.craftory.tech.power.core.block.generators.GeothermalGenerator;
import tech.brettsaunders.craftory.tech.power.core.block.generators.RotaryGenerator;
import tech.brettsaunders.craftory.tech.power.core.block.generators.SolidFuelGenerator;
import tech.brettsaunders.craftory.tech.power.core.block.generators.solar.BasicSolarPanel;
import tech.brettsaunders.craftory.tech.power.core.block.generators.solar.CompactedSolarPanel;
import tech.brettsaunders.craftory.tech.power.core.block.generators.solar.SolarArray;
import tech.brettsaunders.craftory.tech.power.core.block.generators.solar.SolarPanel;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electric_furnace.DiamondElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electric_furnace.EmeraldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electric_furnace.GoldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electric_furnace.IronElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.DiamondElectricFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.EmeraldElectricFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.GoldElectricFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.IronElectricFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.IronFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.macerator.DiamondMacerator;
import tech.brettsaunders.craftory.tech.power.core.block.machine.macerator.EmeraldMacerator;
import tech.brettsaunders.craftory.tech.power.core.block.machine.macerator.GoldMacerator;
import tech.brettsaunders.craftory.tech.power.core.block.machine.macerator.IronMacerator;
import tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser.Magnetiser;
import tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser.MagnetisingTable;
import tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators.BlockBreaker;
import tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators.BlockPlacer;
import tech.brettsaunders.craftory.tech.power.core.block.power_grid.PowerConnector;
import tech.brettsaunders.craftory.tech.power.core.tools.ToolManager;
import tech.brettsaunders.craftory.utils.FileUtils;
import tech.brettsaunders.craftory.utils.Log;

public class Utilities {

  public static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  public final static String DATA_FOLDER;
  public final static String LANG_FOLDER;
  public static FileConfiguration config;
  public static FileConfiguration data;
  public static Metrics metrics;
  public static Properties langProperties;
  public static boolean updateItemGraphics = false;
  private static File configFile = new File(Craftory.plugin.getDataFolder(), "config.yml");
  private static File dataFile = new File(Craftory.plugin.getDataFolder(), "data.yml");
  private static final String UNIT_ENERGY = "Re";
  private static final String UNIT_FLUID = "B";
  private static final DecimalFormat df = new DecimalFormat("###.###");
  @Getter
  private static final HashMap<String, BasicBlocks> basicBlockRegistry;

  private static final Craftory plugin;

  static {
    plugin = Craftory.plugin;
    config = YamlConfiguration
        .loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
    data = YamlConfiguration
        .loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
    DATA_FOLDER = plugin.getDataFolder().getPath() + File.separator + "data";
    LANG_FOLDER = plugin.getDataFolder().getPath() + File.separator + "lang";
    basicBlockRegistry = new HashMap<>();
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

  static void checkVersion() {
    new UpdateChecker(plugin, Craftory.SPIGOT_ID).getVersion(version -> {
      if (Craftory.VERSION.equalsIgnoreCase(version)) {
        Log.info("Plugin is update to date!");
      } else {
        Log.info("There is a new update available!");
      }
    });
  }

  static void createConfigs() {
    config.options().header("Craftory");
    config.addDefault("general.debug", false);
    config.addDefault("general.techEnabled", true);
    config.addDefault("language.locale", "en-GB");
    config.addDefault("generators.solarDuringStorms", true);
    config.addDefault("resourcePack.forcePack", true);
    config.addDefault("fixItemGraphics", false);
    config.addDefault("generators.rotaryGeneratorsSpinWhenFull", false);
    config.addDefault("wrench.powerLoss", 10);
    config.addDefault("ore.blackListedWorlds", Collections.singletonList("exampleBlacklistedWorld"));
    config.addDefault("crafting.blackListedWorlds", Collections.singletonList("exampleBlacklistedWorld"));
    config.addDefault("error_reporting.username", "");
    config.options().copyHeader(true);
    config.options().copyDefaults(true);
    saveConfigFile();
    reloadConfigFile();

    data.options().header("Do Not Touch");
    data.addDefault("reporting.serverUUID", UUID.randomUUID().toString());
    data.addDefault("lastVersion", 0);
    data.options().copyHeader(true);
    data.options().copyDefaults(true);
    saveDataFile();
    reloadDataFile();

    Craftory.lastVersionCode = data.getInt("lastVersion");

    UserBuilder userBuilder = new UserBuilder()
        .setId(data.getString("reporting.serverUUID"));
    if (!Utilities.config.getString("error_reporting.username").isEmpty()) {
      userBuilder.setUsername(Utilities.config.getString("error_reporting.username"));
    }
    Sentry.getContext().setUser(userBuilder.build());

  }

  static void compatibilityUpdater() {
    Log.info("Last version: " + Craftory.lastVersionCode+ " Current version: " + Craftory.thisVersionCode);
    if (Craftory.lastVersionCode < Craftory.thisVersionCode) {
      //Fix all Item Graphics
      Log.info("Updating blocks");
      if (Craftory.lastVersionCode == 0) config.set("fixItemGraphics", true);
      //Version 0.2.1 or before
      if (Craftory.lastVersionCode == 0 || Craftory.lastVersionCode == 200001) {
        //Convert all mushrooms to Stem
        Craftory.customBlockManager.getInactiveChunks().forEach((s, customBlocks) ->
            customBlocks.forEach(customBlock -> {
              plugin.getServer().getScheduler().runTaskLater(plugin,
                  () -> convertMushroomType(customBlock), 2L);
              Log.info("made to stem inactive");
            }));
        Craftory.customBlockManager.getActiveChunks().forEach((s, customBlocks) ->
            customBlocks.forEach(customBlock -> {
              plugin.getServer().getScheduler().runTaskLater(plugin,
                  () -> convertMushroomType(customBlock), 2L);
              if(customBlock.getBlockName().equals(Blocks.DIAMOND_MACERATOR)){
                plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> setToNewDiamondMacerator(customBlock), 2L);
              }
              Log.info("made to stem active");
            }));
      }
    }
    updateItemGraphics = config.getBoolean("fixItemGraphics");
  }

  private static void setToNewDiamondMacerator(CustomBlock customBlock) {
    Block block = customBlock.getLocation().getBlock();
    MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData().clone();
    multipleFacing.setFace(
        BlockFace.UP, true);
    multipleFacing.setFace(BlockFace.DOWN, true);
    multipleFacing.setFace(BlockFace.NORTH,true);
    multipleFacing.setFace(BlockFace.EAST, false);
    multipleFacing.setFace(BlockFace.SOUTH, true);
    multipleFacing.setFace(BlockFace.UP,true);
    multipleFacing.setFace(BlockFace.WEST, true);
    block.setBlockData(multipleFacing);
  }
  private static void convertMushroomType(CustomBlock customBlock) {
    Block block = customBlock.getLocation().getBlock();
    MultipleFacing multipleFacingOG = (MultipleFacing) block.getBlockData().clone();

    block.setType(Material.MUSHROOM_STEM);
    MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData();

    multipleFacing.setFace(
        BlockFace.UP, multipleFacingOG.hasFace(BlockFace.UP));
    multipleFacing.setFace(BlockFace.DOWN, multipleFacingOG.hasFace(BlockFace.DOWN));
    multipleFacing.setFace(BlockFace.NORTH, multipleFacingOG.hasFace(BlockFace.NORTH));
    multipleFacing.setFace(BlockFace.EAST, multipleFacingOG.hasFace(BlockFace.EAST));
    multipleFacing.setFace(BlockFace.SOUTH, multipleFacingOG.hasFace(BlockFace.SOUTH));
    multipleFacing.setFace(BlockFace.WEST, multipleFacingOG.hasFace(BlockFace.WEST));
    block.setBlockData(multipleFacing);
  }

  static void getTranslations() throws IOException {
    String locale = config.getString("language.locale");
    Log.info("Using " + locale + " locale");
    Properties defaultLang = new Properties();
    try (FileInputStream fileInputStream = new FileInputStream(new File(plugin.getDataFolder(),
        "data/default_lang.properties"));InputStreamReader streamReader = new InputStreamReader(new FileInputStream(new File(LANG_FOLDER, locale + ".properties")),
        StandardCharsets.UTF_8)) {
      defaultLang.load(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
      langProperties = new Properties(defaultLang);
      langProperties.load(streamReader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void createDataPath() {
    File file = new File(DATA_FOLDER);
    if (!file.exists()) {
      file.mkdirs();
    } else {
      Craftory.folderExists = true;
    }

    File modelData = new File(plugin.getDataFolder(), "/config/customModelDataV2.yml");
    if (!modelData.exists() || Craftory.lastVersionCode == 0) {
      FileUtils.copyResourcesRecursively(plugin.getClass().getResource("/config"),
          new File(plugin.getDataFolder(), "/config"));
    }

    FileUtils.copyResourcesRecursively(plugin.getClass().getResource("/data"),
        new File(plugin.getDataFolder(), "/data"));

    file = new File(LANG_FOLDER);
    if (!file.exists()) {
      file.mkdirs();
      FileUtils.copyResourcesRecursively(plugin.getClass().getResource("/lang"), file);
    }
  }

  static void startMetrics() {
    metrics = new Metrics(plugin, 7804);
    metrics.addCustomChart(
        new Metrics.SimplePie("debug_enabled", () -> config.getString("general.debug")));
    metrics.addCustomChart(
        new Metrics.SimplePie("tech_enabled", () -> config.getString("general.techEnabled")));
    metrics.addCustomChart(new AdvancedPie("types_of_machines",
        () -> {
          HashMap<String, Integer> valueMap = new HashMap<>();
          //valueMap.put("totalCustomBlocks",Craftory.customBlockManager.statsContainer.getTotalCustomBlocks());
          //valueMap.put("totalPoweredBlocks",Craftory.customBlockManager.statsContainer.getTotalPoweredBlocks());
          valueMap.put("totalCells", CustomBlockManager.statsContainer.getTotalCells());
          valueMap.put("totalGenerators",
              CustomBlockManager.statsContainer.getTotalGenerators());
          valueMap.put("totalPowerConnectors",
              CustomBlockManager.statsContainer.getTotalPowerConnectors());
          valueMap
              .put("totalMachines", CustomBlockManager.statsContainer.getTotalMachines());
          return valueMap;
        }));
    metrics.addCustomChart(new SingleLineChart("total_custom_blocks",
        CustomBlockManager.statsContainer::getTotalCustomBlocks));
    metrics.addCustomChart(new SingleLineChart("total_powered_blocks",
        CustomBlockManager.statsContainer::getTotalPoweredBlocks));
  }

  static void registerCommandsAndCompletions() {
    plugin.getCommand("craftory").setExecutor(new CommandWrapper());
    plugin.getCommand("cr").setExecutor(new CommandWrapper());
    plugin.getCommand("craftory").setTabCompleter(new CommandWrapper());
    plugin.getCommand("cr").setTabCompleter(new CommandWrapper());
  }

  static void registerEvents() {
    new ToolManager();
  }

  static void registerCustomBlocks() {
    CustomBlockFactory customBlockFactory = Craftory.customBlockFactory;
    customBlockFactory.registerCustomBlock(Blocks.IRON_CELL, IronCell.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.GOLD_CELL, GoldCell.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.DIAMOND_CELL, DiamondCell.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.EMERALD_CELL, EmeraldCell.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.IRON_ELECTRIC_FURNACE, IronElectricFurnace.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.GOLD_ELECTRIC_FURNACE, GoldElectricFurnace.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.DIAMOND_ELECTRIC_FURNACE, DiamondElectricFurnace.class, true,
            false);
    customBlockFactory
        .registerCustomBlock(Blocks.EMERALD_ELECTRIC_FURNACE, EmeraldElectricFurnace.class, true,
            false);
    customBlockFactory
        .registerCustomBlock(Blocks.IRON_ELECTRIC_FOUNDRY, IronElectricFoundry.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.GOLD_ELECTRIC_FOUNDRY, GoldElectricFoundry.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.DIAMOND_ELECTRIC_FOUNDRY, DiamondElectricFoundry.class, true,
            false);
    customBlockFactory
        .registerCustomBlock(Blocks.EMERALD_ELECTRIC_FOUNDRY, EmeraldElectricFoundry.class, true,
            false);
    customBlockFactory
        .registerCustomBlock(Blocks.SOLID_FUEL_GENERATOR, SolidFuelGenerator.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.IRON_FOUNDRY, IronFoundry.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.POWER_CONNECTOR, PowerConnector.class, false, false);
    customBlockFactory.registerCustomBlock(Blocks.IRON_MACERATOR, IronMacerator.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.GOLD_MACERATOR, GoldMacerator.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.DIAMOND_MACERATOR, DiamondMacerator.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.EMERALD_MACERATOR, EmeraldMacerator.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.BASIC_SOLAR_PANEL, BasicSolarPanel.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.SOLAR_PANEL, SolarPanel.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.COMPACTED_SOLAR_PANEL, CompactedSolarPanel.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.SOLAR_ARRAY, SolarArray.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.GEOTHERMAL_GENERATOR, GeothermalGenerator.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.ROTARY_GENERATOR, RotaryGenerator.class, true, false);
    customBlockFactory.registerCustomBlock(Blocks.MAGNETISER, Magnetiser.class, true, false);
    customBlockFactory
        .registerCustomBlock(Blocks.MAGNETISING_TABLE, MagnetisingTable.class, false, false);
    customBlockFactory.registerCustomBlock(Blocks.BLOCK_BREAKER, BlockBreaker.class, true, true);
    customBlockFactory.registerCustomBlock(Blocks.BLOCK_PLACER, BlockPlacer.class, true, true);
  }

  static void registerBasicBlocks() {
    basicBlockRegistry.put(Blocks.COPPER_ORE, BasicBlocks.COPPER_ORE);
    basicBlockRegistry.put(Blocks.CRYSTAL_ORE, BasicBlocks.CRYSTAL_ORE);
  }

  static void done() {
    Bukkit.getLogger().info(
        "[" + plugin.getDescription().getPrefix() + "] " + ChatColor.GREEN
            + "Finished Loading!");
  }

  /* Helper Functions */
  public static void reloadConfigFile() {
    if (configFile == null) {
      configFile = new File(plugin.getDataFolder(), "config.yml");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public static void reloadDataFile() {
    if (dataFile == null) {
      dataFile = new File(plugin.getDataFolder(), "data.yml");
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

  public static String getTranslation(String key) {
    String result = langProperties.getProperty(key);
    if (result == null) {
      return "Unknown";
    } else {
      return result;
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

  public static String getChunkWorldID(Chunk chunk) {
    return chunk.getWorld().getName() + "," + getChunkID(chunk);
  }

  public static String convertWorldChunkIDToChunkID(String worldChunkID) {
    return worldChunkID.replaceFirst(".*?,", "");
  }

  public static String getLocationID(Location location) {
    return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
  }

  @Synchronized
  public static Location keyToLoc(String key, World world) {
    String[] locationData = key.split(",");
    return new Location(world, Double.parseDouble(locationData[0]),
        Double.parseDouble(locationData[1]), Double.parseDouble(locationData[2]));
  }

  public static String rawEnergyToPrefixed(Integer energy) {
    String s = Integer.toString(energy);
    int length = s.length();
    if (length < 6) {
      return s + " " + UNIT_ENERGY;
    }
    /*if (length < 7) {
      return s + " " + UNIT;
    }*/
    if (length < 7) {
      return df.format(energy / 1000f) + " K" + UNIT_ENERGY;
    }
    if (length < 10) {
      return df.format(energy / 1000000f) + " M" + UNIT_ENERGY;
    }
    if (length < 13) {
      return df.format(energy / 1000000000f) + " G" + UNIT_ENERGY;
    }
    if (length < 16) {
      return df.format(energy / 1000000000000f) + " T" + UNIT_ENERGY;
    }
    if (length < 19) {
      return df.format(energy / 1000000000000000f) + " P" + UNIT_ENERGY;
    }
    if (length < 22) {
      return df.format(energy / 1000000000000000000f) + " E" + UNIT_ENERGY;
    }
    return "A bukkit load";
  }

  public static String rawFluidToPrefixed(Integer amount) {
    String s = Integer.toString(amount);
    int length = s.length();
    if (length < 6) {
      return s + " m" + UNIT_FLUID;
    }
    if (length < 7) {
      return s + " " + UNIT_FLUID;
    }
    if (length < 10) {
      return df.format(amount / 1000000f) + " K" + UNIT_FLUID;
    }
    if (length < 13) {
      return df.format(amount / 1000000000f) + " M" + UNIT_FLUID;
    }
    if (length < 16) {
      return df.format(amount / 1000000f) + " G" + UNIT_FLUID;
    }
    if (length < 19) {
      return df.format(amount / 1000000f) + " T" + UNIT_FLUID;
    }
    if (length < 22) {
      return df.format(amount / 1000000f) + " P" + UNIT_FLUID;
    }
    return "A bukkit load";
  }
}
