package tech.brettsaunders.craftory;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.event.UserBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.FileUtils;
import tech.brettsaunders.craftory.utils.Logger;


public final class Craftory extends JavaPlugin {

  public static final String VERSION = "0.0.1";

  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;
  private static Craftory plugin = null;
  private static PoweredBlockManager blockPoweredManager = null;
  public static Craftory getInstance() {
    return plugin;
  }
  public static PoweredBlockManager getBlockPoweredManager() {
    return blockPoweredManager;
  }

  @Override
  public void onEnable() {
    Craftory.plugin = this;
    Utilities.pluginBanner();
    Utilities.createDataPath();
    Utilities.createConfigs();
    setupResources();
    Utilities.registerCommandsAndCompletions();
    Utilities.registerEvents();
    /* Needs sorting */
    tickableBaseManager = new TickableBaseManager();
    blockPoweredManager = new PoweredBlockManager();
    powerConnectorManager = new PowerConnectorManager();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    blockPoweredManager.onEnable();
    Utilities.startMetrics();
    Utilities.errorReporting();
  }

  @Override
  public void onDisable() {
    blockPoweredManager.onDisable();
    Utilities.reloadConfigFile();
    Utilities.saveConfigFile();
    plugin = null;
  }


  public void setupResources() {
    File items = new File(getDataFolder().getParentFile(), "ItemsAdder/data/");
    items.mkdirs();
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"), items);
  }

}
