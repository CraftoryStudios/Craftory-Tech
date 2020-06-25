package tech.brettsaunders.craftory;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.api.blocks.CustomBlockFactory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockManager;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGridManagerV2;
import tech.brettsaunders.craftory.utils.FileUtils;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.world.OrePopulator;


public final class Craftory extends JavaPlugin {

  public static final String VERSION = "0.1.0";
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/4305987a904a41eb68d2eb618c0cf640b46d13b6.zip";
  public static final String HASH = "4305987a904a41eb68d2eb618c0cf640b46d13b6";

  public static PowerConnectorManager powerConnectorManager = null;
  public static CustomBlockFactory customBlockFactory;
  public static Craftory plugin = null;
  public static CustomBlockManager customBlockManager;
  public static FileConfiguration customItemConfig;
  public static FileConfiguration customBlocksConfig;
  public static FileConfiguration customRecipeConfig;
  public static CustomBlockTickManager tickManager;
  private static File customItemConfigFile;
  private static File customBlockConfigFile;
  private static File customRecipeConfigFile;
  private static PowerGridManagerV2 blockPoweredManager = null;
  private static OrePopulator orePopulator;

  public static PowerGridManagerV2 getBlockPoweredManager() {
    return blockPoweredManager;
  }

  @Override
  public void onEnable() {
    Craftory.plugin = this;
    customBlockFactory = new CustomBlockFactory();
    Utilities.pluginBanner();
    Utilities.createDataPath();
    Utilities.createConfigs();
    Utilities.registerCustomBlocks();
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
    tickManager = new CustomBlockTickManager();
    Utilities.registerBlocks();
    customBlockManager = new CustomBlockManager();
    customBlockManager.onEnable();
    new RecipeManager();
    blockPoweredManager = new PowerGridManagerV2();
    powerConnectorManager = new PowerConnectorManager();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    Utilities.startMetrics();
    Utilities.done();
    orePopulator = new OrePopulator();
    Bukkit.getWorlds().get(0).getPopulators().add(orePopulator);
    tickManager.runTaskTimer(this, 20L, 1L);

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
    Utilities.reloadConfigFile();
    Utilities.saveConfigFile();
    plugin = null;
    Bukkit.getWorlds().get(0).getPopulators().remove(orePopulator);
  }
}
