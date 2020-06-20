package tech.brettsaunders.craftory;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.FileUtils;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;


public final class Craftory extends JavaPlugin {

  public static final String VERSION = "0.0.1";
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/a2a9baa294addc5a2fab3e4056f40c36a5108a0d.zip";
  public static final String HASH = "a2a9baa294addc5a2fab3e4056f40c36a5108a0d";

  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static FileConfiguration customBlocksConfig;
  public static FileConfiguration customRecipeConfig;
  private static File customItemConfigFile;
  private static File customBlockConfigFile;
  private static File customRecipeConfigFile;
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
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"),
        new File(getDataFolder(), "/data"));
    customBlockConfigFile = new File(getDataFolder(), "data/customBlockConfig.yml");
    customItemConfigFile = new File(getDataFolder(), "data/customItemConfig.yml");
    customRecipeConfigFile = new File(getDataFolder(), "data/customRecipesConfig.yml");
    customItemConfig = YamlConfiguration.loadConfiguration(customItemConfigFile);
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    customRecipeConfig = YamlConfiguration.loadConfiguration(customRecipeConfigFile);
    CustomItemManager.setup(customItemConfig, customBlocksConfig);
    customBlockManager = new CustomBlockManager();
    customBlockManager.onEnable();
    new RecipeManager();
    tickableBaseManager = new TickableBaseManager();
    blockPoweredManager = new PoweredBlockManager();
    powerConnectorManager = new PowerConnectorManager();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    blockPoweredManager.onEnable();
    Utilities.startMetrics();
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
    customBlockManager.onDisable();
    Utilities.reloadConfigFile();
    Utilities.saveConfigFile();
    plugin = null;
  }
}
