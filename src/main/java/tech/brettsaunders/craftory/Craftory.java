package tech.brettsaunders.craftory;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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


public final class Craftory extends JavaPlugin {

  public static String VERSION;
  public static final int SPIGOT_ID = 81151;
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/096cac50626fc99d8673cf43065b095e163576d0.zip";
  public static final String HASH = "096cac50626fc99d8673cf43065b095e163576d0";

  public static PowerConnectorManager powerConnectorManager;
  public static CustomBlockFactory customBlockFactory;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static FileConfiguration customBlocksConfig;
  public static FileConfiguration customRecipeConfig;
  public static CustomBlockTickManager tickManager;
  public static PowerGridManager powerGridManager;

  private static File customItemConfigFile;
  private static File customBlockConfigFile;
  private static File customRecipeConfigFile;

  @Override
  public void onEnable() {
    Craftory.VERSION = this.getDescription().getVersion();
    Craftory.plugin = this;
    Utilities.createConfigs();
    Utilities.createDataPath();
    Utilities.getTranslations();
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
    customItemConfigFile = new File(getDataFolder(), "data/customItemConfig.yml");
    customRecipeConfigFile = new File(getDataFolder(), "data/customRecipesConfig.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    customRecipeConfig = YamlConfiguration.loadConfiguration(customRecipeConfigFile);
    CustomItemManager.setup(customItemConfig, customBlocksConfig);
    tickManager = new CustomBlockTickManager();
    Utilities.registerBlocks();
    customBlockManager = new CustomBlockManager();
    customBlockFactory.registerStats();
    customBlockManager.onEnable();
    new WorldGenHandler();
    new RecipeManager();
    new PoweredBlockEvents();
    powerConnectorManager = new PowerConnectorManager();
    powerGridManager = new PowerGridManager();
    powerGridManager.onEnable();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    Utilities.startMetrics();
    Utilities.done();
    tickManager.runTaskTimer(this, 20L, 1L);

    Utilities.setupAdvancements();
    //Testing
    this.getCommand("crtesting").setExecutor(new TestingCommand());
  }

  @Override
  public void onDisable() {
    try {
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
}
