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

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.dsn.InvalidDsnException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.api.blocks.CustomBlockFactory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockEvents;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.recipes.RecipeBook;
import tech.brettsaunders.craftory.api.recipes.RecipeBookEvents;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.effect.EnergyDisplayManager;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerGridManager;
import tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager;
import tech.brettsaunders.craftory.testing.TestingCommand;
import tech.brettsaunders.craftory.utils.ConfigManager;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.world.WorldGenHandler;


public final class Craftory extends JavaPlugin implements Listener {

  public static Craftory instance = null;
  @Getter
  @Setter
  private int lastVersionCode;
  @Getter
  private int thisVersionCode;

  //Managers
  @Getter
  private final PowerConnectorManager powerConnectorManager = new PowerConnectorManager();
  @Getter
  private final CustomBlockFactory customBlockFactory = new CustomBlockFactory();
  @Getter
  private final CustomBlockManager customBlockManager = new CustomBlockManager();
  @Getter
  private  final PoweredToolManager poweredToolManager = new PoweredToolManager();
  @Getter
  private final CustomBlockTickManager tickManager = new CustomBlockTickManager();
  @Getter
  private final PowerGridManager powerGridManager = new PowerGridManager();
  @Getter
  private final RecipeBookEvents recipeBookEvents = new RecipeBookEvents();
  @Getter
  private ProtocolManager packetManager;
  @Getter
  private HashSet<String> loadedPlugins = new HashSet<>();

  @Override
  public void onEnable() {
    instance = this;
    setupSentry();
    try {
      if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
        Log.error("ProtocolLib is needed to run the latest version of craftory!");
        getServer().getPluginManager().disablePlugin(this);
      } else {
        packetManager = ProtocolLibrary.getProtocolManager();
      }
      loadedPlugins =
          (HashSet<String>) Arrays.stream(this.getServer().getPluginManager().getPlugins()).map(Plugin::getName).collect(
          Collectors.toSet());

      generateVersionCode();
      this.getServer().getPluginManager().registerEvents(this, this);
      Utilities.createDataPath();
      Utilities.createConfigs();
      Utilities.getTranslations();
      Utilities.pluginBanner();
      Utilities.checkVersion();
      Utilities.registerBasicBlocks();
      Utilities.registerCustomBlocks();
      Utilities.registerCommandsAndCompletions();
      Utilities.registerEvents();
      ConfigManager.setupConfigs();
      CustomItemManager.setup();
      Utilities.startMetrics();
      Utilities.done();

      //Events
      Events.registerEvents(powerGridManager);
      Events.registerEvents(recipeBookEvents);

      //Managers
      customBlockManager.setup();
      poweredToolManager.setup();
      new WorldGenHandler();
      new PoweredBlockEvents();
      if (Utilities.config.getBoolean("resourcePack.forcePack"))
        new ResourcePackEvents();

      //Tasks
      Tasks.runTaskTimer(new EnergyDisplayManager(), 30L, 30L);
      Tasks.runTaskTimer(tickManager, 20L, 1L);

      //Testing
      this.getCommand("crtesting").setExecutor(new TestingCommand());

    } catch (Exception e) {
      sentryLog(e);
    }
  }

  @EventHandler
  public void onServerLoaded(ServerLoadEvent e) {
    try {
      loadedPlugins =
          (HashSet<String>) Arrays.stream(this.getServer().getPluginManager().getPlugins()).map(Plugin::getName).collect(
          Collectors.toSet());
      powerGridManager.onEnable();
      getServer().getPluginManager().registerEvents(powerConnectorManager, this);
      new RecipeManager();
      new RecipeBook();
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
      ConfigManager.saveConfigs();
      recipeBookEvents.onDisable();
      customBlockManager.onDisable();
      powerGridManager.onDisable();
      Utilities.reloadConfigFile();
      Utilities.saveConfigFile();
    } catch (Exception e) {
      sentryLog(e);
    }
  }

  private void generateVersionCode() {
    String[] subVersions = getDescription().getVersion().split("\\.");
    StringBuilder resultString = new StringBuilder();
    for (String subVersion : subVersions) {
      resultString.append(StringUtils.leftPad(subVersion, 5, "0"));
    }
    thisVersionCode = Integer.parseInt(resultString.toString());
  }

  public boolean isPluginLoaded(String name) {
    return loadedPlugins.contains(name);
  }
  private void setupSentry() {
    SentryClient sentryClient;
    // Setup connection to Sentry.io
    try {
      sentryClient = Sentry.init("https://6b3f8706e5e74f39bbd037a30e3841f7@o399729.ingest.sentry"
          + ".io/5257818?stacktrace.app.packages=tech.brettsaunders.craftory");
    } catch(InvalidDsnException | IllegalArgumentException e) {
      Log.error("Provided Sentry DSN is invalid:", ExceptionUtils.getStackTrace(e));
      return;
    }

    // Default data
    sentryClient.setServerName(this.getServer().getName());
    sentryClient.setRelease(this.getDescription().getVersion());

    Log.info("Sentry Enabled!");
  }
}
