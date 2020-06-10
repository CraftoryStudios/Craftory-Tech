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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.FileUtils;


public final class Craftory extends JavaPlugin {

  private static final String VERSION = "0.0.1";
  public static SentryClient sentry;
  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;
  private static Craftory plugin = null;
  private static boolean debugMode = false;
  private static PoweredBlockManager blockPoweredManager = null;
  FileConfiguration config = getConfig();

  public static Craftory getInstance() {
    return plugin;
  }

  public static boolean getDebugMode() {
    return debugMode;
  }

  public static PoweredBlockManager getBlockPoweredManager() {
    return blockPoweredManager;
  }

  @Override
  public void onEnable() {
    // Plugin startup logic
    plugin = this;
    //bStats
    int pluginId = 7804;
    Metrics metrics = new Metrics(this, pluginId);
    //Sentry
    Sentry.init(
        "https://6b3f8706e5e74f39bbd037a30e3841f7@o399729.ingest.sentry.io/5257818?debug=false&&environment=WIP&&release="
            + VERSION);
    sentry = SentryClientFactory.sentryClient();
    //Setup
    resourceSetup();
    Sentry.getContext().setUser(new UserBuilder().setId(config.getString("serverUUID")).build());
    Sentry.getContext().addTag("BukkitVersion", Bukkit.getBukkitVersion());
    Sentry.getContext().addExtra("Plugins", Bukkit.getPluginManager().getPlugins());
    debugMode = config.getBoolean("debugMode");

    //General Classes
    tickableBaseManager = new TickableBaseManager();
    blockPoweredManager = new PoweredBlockManager();
    powerConnectorManager = new PowerConnectorManager(); //TODO Loading
    //Register Events
    getServer().getPluginManager().registerEvents(powerConnectorManager, this);
    blockPoweredManager.onEnable();
  }

  @Override
  public void onDisable() {
    //Save Data
    blockPoweredManager.onDisable();
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("toggleDebugMode")) {
      debugMode = !debugMode;
      sender.sendMessage("Mode switch to " + debugMode);
      return true;
    }
    return false;
  }

  public void resourceSetup() {
    //Load Data
    config.addDefault("enableTech", true);
    config.addDefault("debugMode", false);
    config.addDefault("serverUUID", UUID.randomUUID().toString());
    config.options().copyDefaults(true);
    saveConfig();

    File items = new File(getDataFolder().getParentFile(), "ItemsAdder/data/");
    items.mkdirs();
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"), items);
  }

}
