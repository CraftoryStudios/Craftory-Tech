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
import tech.brettsaunders.craftory.utils.ResourcePackEvents;


public final class Craftory extends JavaPlugin {

  public static final String VERSION = "0.0.1";
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/4158c81bf79cbbfb594c34f94010ffe495e2cec9.zip";
  public static final String HASH = "4158c81bf79cbbfb594c34f94010ffe495e2cec9";

  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static File customItemConfigFile;
  public static FileConfiguration customBlocksConfig;
  public static File customBlockConfigFile;
  private static PoweredBlockManager blockPoweredManager = null;

  public static PoweredBlockManager getBlockPoweredManager() {
    return blockPoweredManager;
  }

  @Override
  public void onEnable() {
    Craftory.plugin = this;
    Utilities.pluginBanner();
    Utilities.createDataPath();
    Utilities.createConfigs();
    Utilities.registerCommandsAndCompletions();
    Utilities.registerEvents();
    new ResourcePackEvents();
    /* Needs sorting */
    customBlockConfigFile = new File(getDataFolder(), "customBlockConfig.yml");
    customItemConfigFile = new File(getDataFolder(), "customItemConfig.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    CustomItemManager.setup(customItemConfig, customBlocksConfig);

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
}
