package tech.brettsaunders.craftory;

import com.google.common.base.Strings;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.event.UserBuilder;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.commands.CommandWrapper;

public class Utilities {
  public final static String DATA_FOLDER;
  public static FileConfiguration config;
  public static FileConfiguration data;
  public static SentryClient sentry;
  private static File configFile = new File(Craftory.getInstance().getDataFolder(), "config.yml");
  private static File dataFile = new File(Craftory.getInstance().getDataFolder(), "data.yml");

  static {
    config = YamlConfiguration.loadConfiguration(new File(Craftory.getInstance().getDataFolder(), "config.yml"));
    data = YamlConfiguration.loadConfiguration(new File(Craftory.getInstance().getDataFolder(), "data.yml"));
    DATA_FOLDER = Craftory.getInstance().getDataFolder().getPath() + File.separator + "data";
  }

  static void pluginBanner() {
    drawBanner("&2-------------------------------------------");
    drawBanner("&2   _____            __ _                   ");
    drawBanner("&2  / ____|          / _| |                  ");
    drawBanner("&2 | |     _ __ __ _| |_| |_ ___  _ __ _   _ ");
    drawBanner("&2 | |    | '__/ _` |  _| __/ _ \\| '__| | | |");
    drawBanner("&2 | |____| | | (_| | | | || (_) | |  | |_| |");
    drawBanner("&2  \\_____|_|  \\__,_|_|  \\__\\___/|_|   \\__, |");
    drawBanner("&2                                      __/ |");
    drawBanner("&2                                     |___/ ");
    drawBanner("&2-------------------------------------------");
  }

  static void createConfigs() {
    config.options().header("Craftory");
    config.addDefault("general.debug", false);
    config.addDefault("general.techEnabled", true);
    config.options().copyHeader(true);
    config.options().copyDefaults(true);
    saveConfigFile();
    reloadConfigFile();

    data.options().header("Do Not Touch");
    data.addDefault("reporting.serverUUID", UUID.randomUUID().toString());
    data.options().copyHeader(true);
    data.options().copyDefaults(true);
    saveDataFile();
    reloadDataFile();
  }

  static void createDataPath() {
    File file = new File(DATA_FOLDER);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  static void startMetrics() {
    Metrics metrics = new Metrics(Craftory.getInstance(), 7804);
    metrics.addCustomChart(new Metrics.SimplePie("debugEnabled", () -> config.getString("general.debug")));
    metrics.addCustomChart(new Metrics.SimplePie("techEnabled", () -> config.getString("general.techEnabled")));
  }

  static void registerCommandsAndCompletions() {
    Craftory.getInstance().getCommand("craftory").setExecutor(new CommandWrapper());
    Craftory.getInstance().getCommand("cf").setExecutor(new CommandWrapper());
    Craftory.getInstance().getCommand("craftory").setTabCompleter(new CommandWrapper());
    Craftory.getInstance().getCommand("cf").setTabCompleter(new CommandWrapper());
  }

  static void registerEvents() {

  }

  static void errorReporting() {
    Sentry.init(
        "https://6b3f8706e5e74f39bbd037a30e3841f7@o399729.ingest.sentry.io/5257818?debug=false&&environment=WIP&&release="
            + Craftory.VERSION);
    sentry = SentryClientFactory.sentryClient();
    Sentry.getContext().setUser(new UserBuilder().setId(data.getString("reporting.serverUUID")).build());
    Sentry.getContext().addTag("BukkitVersion", Bukkit.getBukkitVersion());
    Sentry.getContext().addExtra("Plugins", Bukkit.getPluginManager().getPlugins());
  }

  /* Helper Functions */
  public static void reloadConfigFile() {
    if (configFile == null) {
      configFile = new File(Craftory.getInstance().getDataFolder(), "config.yml");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public static void reloadDataFile() {
    if (dataFile == null) {
      dataFile = new File(Craftory.getInstance().getDataFolder(), "data.yml");
    }
    data = YamlConfiguration.loadConfiguration(dataFile);
  }

  public static void saveDataFile() {
    if (data == null || dataFile == null) {
      return;
    }
    try {
      data.save(dataFile);
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, "Could not save " + dataFile, ex);
    }
  }

  public static void saveConfigFile() {
    if (config == null || configFile == null) {
      return;
    }
    try {
      config.save(configFile);
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
    }
  }

  public static void msg(final CommandSender s, String msg) {
    if (s instanceof Player) {
      msg = ChatColor.translateAlternateColorCodes('&', msg);
    } else {
      msg = ChatColor.translateAlternateColorCodes('&', "[Craftory]" + msg);
    }
    s.sendMessage(msg);
  }

  private static void drawBanner(final String message) {
    Bukkit.getServer().getConsoleSender().sendMessage(
        ChatColor.translateAlternateColorCodes('&', message));
  }

}
