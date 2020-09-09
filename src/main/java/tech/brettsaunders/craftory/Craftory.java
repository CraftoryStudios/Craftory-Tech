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

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
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
import tech.brettsaunders.craftory.api.recipes.RecipeBook;
import tech.brettsaunders.craftory.api.recipes.RecipeBookEvents;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.effect.EnergyDisplayManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGridManager;
import tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager;
import tech.brettsaunders.craftory.testing.TestingCommand;
import tech.brettsaunders.craftory.utils.DataConfigUtils;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.world.WorldGenHandler;


public final class Craftory extends JavaPlugin implements Listener {

  public static final int SPIGOT_ID = 81151;
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/beb6b420d791fb64f6321b1a8b9dc70d44f9a955.zip";
  public static final String HASH = "beb6b420d791fb64f6321b1a8b9dc70d44f9a955";
  public static String VERSION;
  public static PowerConnectorManager powerConnectorManager;
  public static CustomBlockFactory customBlockFactory;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static ProtocolManager packetManager;
  public static PoweredToolManager poweredToolManager;

  public static FileConfiguration customModelDataConfig;
  public static FileConfiguration customBlocksConfig;
  public static FileConfiguration customRecipeConfig;
  public static FileConfiguration serverDataConfig;
  public static CustomBlockTickManager tickManager;
  public static PowerGridManager powerGridManager;
  public static RecipeBookEvents recipeBookEvents;
  public static int lastVersionCode;
  public static int thisVersionCode;
  public static boolean folderExists = false;
  private static File customItemConfigFile;
  private static File customBlockConfigFile;
  private static File customRecipeConfigFile;
  private static File customModelDataFile;
  private static File serverDataFile;

  private static int generateVersionCode() {
    String[] subVersions = VERSION.split("\\.");
    StringBuffer resultString = new StringBuffer();
    for (String subVersion : subVersions) {
      resultString.append(StringUtils.leftPad(subVersion, 5, "0"));
    }
    int result = Integer.parseInt(resultString.toString());
    return result;
  }

  @SneakyThrows
  @Override
  public void onEnable() {
    if(getServer().getPluginManager().getPlugin("ProtocolLib") == null){
      Logger.error("ProtocolLib is needed to run the latest version of craftory!");
      getServer().getPluginManager().disablePlugin(this);
    }
    packetManager = ProtocolLibrary.getProtocolManager();
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
    poweredToolManager = new PoweredToolManager(); //Must be before CustomItemManager
    customBlockConfigFile = new File(getDataFolder(), "data/customBlockConfig.yml");
    customItemConfigFile = new File(getDataFolder(), "data/customItemConfig.yml");
    customRecipeConfigFile = new File(getDataFolder(), "config/customRecipesConfig.yml");
    customModelDataFile = new File(getDataFolder(), "config/customModelDataV2.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    customRecipeConfig = YamlConfiguration.loadConfiguration(customRecipeConfigFile);
    customRecipeConfig.save(customRecipeConfigFile);
    customModelDataConfig = YamlConfiguration.loadConfiguration(customModelDataFile);
    Optional<FileConfiguration> recipesDefaults = Optional.ofNullable(YamlConfiguration.loadConfiguration(new File(Craftory.plugin.getDataFolder(), "data/customRecipesConfig.yml")));
    recipesDefaults.ifPresent(source -> DataConfigUtils.copyDefaults(source, customRecipeConfig));
    customRecipeConfig.save(customRecipeConfigFile);
    CustomItemManager.setup(customItemConfig, customBlocksConfig, customModelDataConfig);
    customBlockManager = new CustomBlockManager();
    customBlockFactory.registerStats();
    new WorldGenHandler();
    new PoweredBlockEvents();
    Utilities.startMetrics();
    Utilities.done();

    //Tasks
    Tasks.runTaskTimer(new EnergyDisplayManager(), 30L, 30L);
    Tasks.runTaskTimer(tickManager, 20L, 1L);
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
    new RecipeBook();
    recipeBookEvents = new RecipeBookEvents();
    Utilities.compatibilityUpdater();
  }

  @Override
  public void onDisable() {
    try {
      Utilities.data.set("lastVersion", thisVersionCode);
      Utilities.saveDataFile();
      customItemConfig.save(customItemConfigFile);
      customBlocksConfig.save(customBlockConfigFile);
      customRecipeConfig.save(customRecipeConfigFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    recipeBookEvents.onDisable();
    customBlockManager.onDisable();
    powerGridManager.onDisable();
    Utilities.reloadConfigFile();
    Utilities.saveConfigFile();
    plugin = null;
  }
}
