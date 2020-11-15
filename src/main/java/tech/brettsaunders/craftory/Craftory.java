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

import static tech.brettsaunders.craftory.Utilities.checkMinecraftVersion;
import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.dsn.InvalidDsnException;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
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
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerGridManager;
import tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager;
import tech.brettsaunders.craftory.utils.DataConfigUtils;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.utils.Version;
import tech.brettsaunders.craftory.world.WorldGenHandler;


public final class Craftory extends JavaPlugin implements Listener {

  public static final int SPIGOT_ID = 81151;
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/1c217b5f61b5795b09fd6f2f3d0d878ece3f5ae6.zip";
  public static final String HASH = "1c217b5f61b5795b09fd6f2f3d0d878ece3f5ae6";
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
  private SentryClient sentryClient;
  private static HashSet<String> loadedPlugins = new HashSet<>();

  public static final Version MAX_SUPPORTED_MC = new Version("1.16.4");
  public static Version mcVersion;

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
    Craftory.plugin = this;
    Craftory.VERSION = this.getDescription().getVersion();
    mcVersion = new Version(getServer());
    if (checkMinecraftVersion()) {
      return;
    }

    setupSentry();
    try {
      if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
        Log.error("ProtocolLib is needed to run the latest version of craftory!");
        getServer().getPluginManager().disablePlugin(this);
      }
      loadedPlugins = (HashSet<String>) Arrays.stream(plugin.getServer().getPluginManager().getPlugins()).map(Plugin::getName).collect(
          Collectors.toSet());
      packetManager = ProtocolLibrary.getProtocolManager();
      thisVersionCode = generateVersionCode();
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
      Optional<FileConfiguration> recipesDefaults = Optional.of(YamlConfiguration
          .loadConfiguration(
              new File(Craftory.plugin.getDataFolder(), "data/customRecipesConfig.yml")));
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
      //this.getCommand("crtesting").setExecutor(new TestingCommand());

    } catch (Exception e) {
      sentryLog(e);
    }
  }

  @EventHandler
  public void onServerLoaded(ServerLoadEvent e) {
    try {
      loadedPlugins = (HashSet<String>) Arrays.stream(plugin.getServer().getPluginManager().getPlugins()).map(Plugin::getName).collect(
          Collectors.toSet());
      powerConnectorManager = new PowerConnectorManager();
      powerGridManager = new PowerGridManager();
      powerGridManager.onEnable();
      getServer().getPluginManager().registerEvents(powerConnectorManager, this);
      new RecipeManager();
      new RecipeBook();
      recipeBookEvents = new RecipeBookEvents();
      Utilities.compatibilityUpdater();
    } catch (Exception exception) {
      sentryLog(exception);
    }
  }

  @Override
  public void onDisable() {
    try {
      Utilities.data.set("lastVersion", thisVersionCode);
      Utilities.saveDataFile();
      if (Objects.nonNull(customItemConfigFile) && Objects.nonNull(customItemConfig)) customItemConfig.save(customItemConfigFile);
      if (Objects.nonNull(customBlockConfigFile) && Objects.nonNull(customBlocksConfig)) customBlocksConfig.save(customBlockConfigFile);
      if (Objects.nonNull(customRecipeConfigFile) && Objects.nonNull(customRecipeConfig)) customRecipeConfig.save(customRecipeConfigFile);
      if (Objects.nonNull(recipeBookEvents)) recipeBookEvents.onDisable();
      if (Objects.nonNull(customBlockManager)) customBlockManager.onDisable();
      if (Objects.nonNull(powerGridManager)) powerGridManager.onDisable();
      Utilities.reloadConfigFile();
      Utilities.saveConfigFile();
      plugin = null;
    } catch (Exception e) {
      sentryLog(e);
    }
  }

  public boolean isPluginLoaded(String name) {
    return loadedPlugins.contains(name);
  }
  private void setupSentry() {
    // Setup connection to Sentry.io
    try {
      sentryClient = Sentry.init("https://3981ae28ec8444368fdc4397121fcac9@o467455.ingest.sentry.io/5493918"
          + "?stacktrace.app.packages=tech.brettsaunders.craftory");
    } catch(InvalidDsnException | IllegalArgumentException e) {
      Log.error("Provided Sentry DSN is invalid:", ExceptionUtils.getStackTrace(e));
      return;
    }

    // Default data
    sentryClient.setServerName(this.getServer().getName());
    sentryClient.setRelease(VERSION);


    Log.info("Sentry Enabled!");
  }
}
