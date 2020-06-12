package tech.brettsaunders.craftory;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.FileUtils;


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
    Utilities.done();
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
