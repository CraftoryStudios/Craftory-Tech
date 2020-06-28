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
import tech.brettsaunders.craftory.api.blocks.PoweredBlockEvents;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.recipes.RecipeManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGridManager;
import tech.brettsaunders.craftory.utils.FileUtils;
import tech.brettsaunders.craftory.utils.ResourcePackEvents;
import tech.brettsaunders.craftory.world.OrePopulator;


public final class Craftory extends JavaPlugin {

  public static String VERSION;
  public static final int SPIGOT_ID = 12345;
  public static final String RESOURCE_PACK = "https://download.mc-packs.net/pack/05d7f631b06ccb9eaff2e6ffb25b3559678f193e.zip";
  public static final String HASH = "05d7f631b06ccb9eaff2e6ffb25b3559678f193e";

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
  private static OrePopulator orePopulator;

  @Override
  public void onEnable() {
    Craftory.VERSION = this.getDescription().getVersion();
    Craftory.plugin = this;
    customBlockFactory = new CustomBlockFactory();
    Utilities.pluginBanner();
    Utilities.checkVersion();
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
    new PoweredBlockEvents();
    powerGridManager = new PowerGridManager();
    powerConnectorManager = new PowerConnectorManager();
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    Utilities.startMetrics();
    Utilities.done();
    orePopulator = new OrePopulator();
    Bukkit.getWorlds().get(0).getPopulators().add(orePopulator);
    tickManager.runTaskTimer(this, 20L, 1L);
    Utilities.setupAdvancements();
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
