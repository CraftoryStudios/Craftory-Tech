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

import java.io.File;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.api.blocks.CustomBlockFactory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockEvents;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGridManager;
import tech.brettsaunders.craftory.testing.TestingCommand;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.world.WorldGenHandler;


public final class Craftory extends JavaPlugin implements Listener {

  public static String VERSION;
  public static final int SPIGOT_ID = 81151;
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/a4583aaebbc1d70e303abae0be49fa0514aae337.zip";
  public static final String HASH = "a4583aaebbc1d70e303abae0be49fa0514aae337";

  public static PowerConnectorManager powerConnectorManager;
  public static CustomBlockFactory customBlockFactory;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;

  public static FileConfiguration customModelDataConfig;
  public static FileConfiguration customBlocksConfig;
  public static FileConfiguration customRecipeConfig;
  public static FileConfiguration serverDataConfig;
  public static CustomBlockTickManager tickManager;
  public static PowerGridManager powerGridManager;

  private static File customItemConfigFile;
  private static File customBlockConfigFile;
  private static File customRecipeConfigFile;
  private static File customModelDataFile;
  private static File serverDataFile;

  public static int lastVersionCode = 0;
  public static int thisVersionCode;
  public static boolean folderExists = false;

  @SneakyThrows
  @Override
  public void onEnable() {
    Craftory.VERSION = this.getDescription().getVersion();
    thisVersionCode = generateVersionCode();
    Craftory.plugin = this;
    this.getServer().getPluginManager().registerEvents(this, this);

    Utilities.createDataPath();
    Utilities.createConfigs();
    Utilities.getTranslations();
    tickManager = new CustomBlockTickManager();
    customBlockFactory = new CustomBlockFactory();
    Utilities.pluginBanner();
    Utilities.checkVersion();
    Utilities.registerBasicBlocks();
    Utilities.registerCustomBlocks();
    Utilities.registerCommandsAndCompletions();
    Utilities.registerEvents();
    if (Utilities.config.getBoolean("resourcePack.forcePack")) {
      new ResourcePackEvents();
    }
    customBlockConfigFile = new File(getDataFolder(), "data/customBlockConfig.yml");
    customItemConfigFile = new File(getDataFolder(),"data/customItemConfig.yml");
    customRecipeConfigFile = new File(getDataFolder(),"data/customRecipesConfig.yml");
    customModelDataFile = new File(getDataFolder(), "config/customModelDataV2.yml");
    serverDataFile = new File(getDataFolder(), "data/serverData.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    customRecipeConfig = YamlConfiguration.loadConfiguration(customRecipeConfigFile);
    customModelDataConfig = YamlConfiguration.loadConfiguration(customModelDataFile);
    serverDataConfig = YamlConfiguration.loadConfiguration(serverDataFile);
    lastVersionCode = serverDataConfig.getInt("lastVersion");
    CustomItemManager.setup(customItemConfig, customBlocksConfig, customModelDataConfig);
    customBlockManager = new CustomBlockManager();
    customBlockFactory.registerStats();
    new WorldGenHandler();
    new PoweredBlockEvents();
    Utilities.startMetrics();
    Utilities.done();
    tickManager.runTaskTimer(this, 20L, 1L);

    Utilities.setupAdvancements();
    //Testing
    this.getCommand("crtesting").setExecutor(new TestingCommand());
  }

  @EventHandler
  public void onServerLoaded(ServerLoadEvent e) {
    powerConnectorManager = new PowerConnectorManager();
    powerGridManager = new PowerGridManager();
    powerGridManager.onEnable();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    new RecipeManager();
    Utilities.compatibilityUpdater();
  }


  @Override
  public void onDisable() {
    try {
      serverDataConfig.set("lastVersion",thisVersionCode);
      serverDataConfig.save(serverDataFile);
      customItemConfig.save(customItemConfigFile);
      customBlocksConfig.save(customBlockConfigFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    customBlockManager.onDisable();
    powerGridManager.onDisable();
    Utilities.reloadConfigFile();
    Utilities.saveConfigFile();
    plugin = null;
  }

  private static int generateVersionCode() {
    String[] subVersions = VERSION.split("\\.");
    String resultString = "";
    for (String subVersion : subVersions) {
      resultString += StringUtils.leftPad(subVersion, 5, "0");
    }
    int result = Integer.parseInt(resultString);
    return result;
  }
}
