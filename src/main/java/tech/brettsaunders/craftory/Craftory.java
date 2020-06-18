package tech.brettsaunders.craftory;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.FileUtils;


public final class Craftory extends JavaPlugin {

  public static final String VERSION = "0.0.1";

  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;
  public static Craftory plugin = null;
  private static PoweredBlockManager blockPoweredManager = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static File customItemConfigFile;
  public static FileConfiguration customBlocksConfig;
  public static File customBlockConfigFile;

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
    customBlockConfigFile = new File(getDataFolder(), "customBlockConfig.yml");
    customItemConfigFile = new File(getDataFolder(), "customItemConfig.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customItemConfig.addDefault("items", null);
    customItemConfig.options().copyDefaults(true);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    CustomItemManager.setup(customItemConfig,customBlocksConfig);

    customBlockManager = new CustomBlockManager();
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
    try {
      customItemConfig.save(customItemConfigFile);
      customBlocksConfig.save(customBlockConfigFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
