package tech.brettsaunders.craftory.utils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.brettsaunders.craftory.Craftory;

public class ConfigManager {

  //Config File Paths
  private static final String DATA_CUSTOM_ITEM_CONFIG = "data/customItemConfig.yml";
  private static final String DATA_CUSTOM_BLOCK_CONFIG = "data/customBlockConfig.yml";
  private static final String CONFIG_CUSTOM_RECIPES_CONFIG = "config/customRecipesConfig.yml";
  private static final String CONFIG_CUSTOM_MODEL_DATA = "config/customModelDataV2.yml";

  //Configs
  @Getter
  private static FileConfiguration customModelDataConfig;
  @Getter
  private static FileConfiguration customBlocksConfig;
  @Getter
  private static FileConfiguration customRecipeConfig;
  @Getter
  private static FileConfiguration customItemConfig;

  public static void setupConfigs() {
    try {
      File dataFolder = Craftory.instance.getDataFolder();

      customItemConfig = YamlConfiguration.loadConfiguration(new File(dataFolder,
          DATA_CUSTOM_ITEM_CONFIG));
      customBlocksConfig = YamlConfiguration.loadConfiguration(new File(dataFolder,
          DATA_CUSTOM_BLOCK_CONFIG));
      customRecipeConfig = YamlConfiguration.loadConfiguration(new File(dataFolder,
          CONFIG_CUSTOM_RECIPES_CONFIG));
      customRecipeConfig.save(new File(dataFolder,
          CONFIG_CUSTOM_RECIPES_CONFIG));
      customModelDataConfig = YamlConfiguration.loadConfiguration(new File(dataFolder,
          CONFIG_CUSTOM_MODEL_DATA));

      Optional<FileConfiguration> recipesDefaults = Optional.of(YamlConfiguration
          .loadConfiguration(
              new File(Craftory.instance.getDataFolder(), "data/customRecipesConfig.yml")));
      recipesDefaults.ifPresent(source -> DataConfigUtils.copyDefaults(source, customRecipeConfig));
      customRecipeConfig.save(new File(dataFolder,
          CONFIG_CUSTOM_RECIPES_CONFIG));
    } catch (IOException e) {
      Log.error("Saving config failed");
    }
  }

  public static String getDataFolder() {
    return Craftory.instance.getDataFolder() + File.separator + "data";
  }

  public static void saveConfigs(){
    File dataFolder = Craftory.instance.getDataFolder();
    try {
      customItemConfig.save(new File(dataFolder,
          DATA_CUSTOM_ITEM_CONFIG));
      customBlocksConfig.save(new File(dataFolder,
          DATA_CUSTOM_BLOCK_CONFIG));
      customRecipeConfig.save(new File(dataFolder,
          CONFIG_CUSTOM_RECIPES_CONFIG));
    } catch (IOException e) {
      Log.error("Failed config end save");
    }
  }

}
