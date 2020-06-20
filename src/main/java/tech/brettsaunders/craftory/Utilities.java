package tech.brettsaunders.craftory;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.event.UserBuilder;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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
import tech.brettsaunders.craftory.utils.Logger;

public class Utilities {

  public final static String DATA_FOLDER;
  public static FileConfiguration config;
  public static FileConfiguration data;
  public static SentryClient sentry;
  private static File configFile = new File(Craftory.plugin.getDataFolder(), "config.yml");
  private static File dataFile = new File(Craftory.plugin.getDataFolder(), "data.yml");

  static {
    config = YamlConfiguration
        .loadConfiguration(new File(Craftory.plugin.getDataFolder(), "config.yml"));
    data = YamlConfiguration
        .loadConfiguration(new File(Craftory.plugin.getDataFolder(), "data.yml"));
    DATA_FOLDER = Craftory.plugin.getDataFolder().getPath() + File.separator + "data";
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
    Metrics metrics = new Metrics(Craftory.plugin, 7804);
    metrics.addCustomChart(
        new Metrics.SimplePie("debugEnabled", () -> config.getString("general.debug")));
    metrics.addCustomChart(
        new Metrics.SimplePie("techEnabled", () -> config.getString("general.techEnabled")));
  }

  static void registerCommandsAndCompletions() {
    Craftory.plugin.getCommand("craftory").setExecutor(new CommandWrapper());
    Craftory.plugin.getCommand("cf").setExecutor(new CommandWrapper());
    Craftory.plugin.getCommand("craftory").setTabCompleter(new CommandWrapper());
    Craftory.plugin.getCommand("cf").setTabCompleter(new CommandWrapper());
  }

  static void registerEvents() {

  }

  static void errorReporting() {
    Logger.info("///////// Please ignore Sentry.io Warnings ///////");
    Sentry.init(
        "https://6b3f8706e5e74f39bbd037a30e3841f7@o399729.ingest.sentry.io/5257818?debug=false&&environment=WIP&&release="
            + Craftory.VERSION);
    sentry = SentryClientFactory.sentryClient();
    Sentry.getContext()
        .setUser(new UserBuilder().setId(data.getString("reporting.serverUUID")).build());
    Sentry.getContext().addTag("Bukkit Version", Bukkit.getBukkitVersion());
    Sentry.getContext().addExtra("Plugins", Bukkit.getPluginManager().getPlugins());
    Logger.info("///////// Please ignore Sentry.io Warnings ///////");
  }

  static void done() {
    Bukkit.getLogger().info(
        "[" + Craftory.plugin.getDescription().getPrefix() + "] " + ChatColor.GREEN
            + "Finished Loading!");
  }

  /* Helper Functions */
  public static void reloadConfigFile() {
    if (configFile == null) {
      configFile = new File(Craftory.plugin.getDataFolder(), "config.yml");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public static void reloadDataFile() {
    if (dataFile == null) {
      dataFile = new File(Craftory.plugin.getDataFolder(), "data.yml");
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

  private static String UNIT = "Re";
  private static DecimalFormat df = new DecimalFormat("###.###");
  public static String rawToPrefixed(Integer energy) {
    String s = Integer.toString(energy);
    int length = s.length();
    //if(length < 3) return s + " " + UNIT;
    if(length < 7) return s + " " + UNIT;
    //if(length < 7) return df.format(energy/1000f) +" K" + UNIT;
    if(length < 10) return df.format(energy/1000000f) +" M" + UNIT;
    if(length < 13) return df.format(energy/1000000000f) +" G" + UNIT;
    if(length < 16) return df.format(energy/1000000000000f) +" T" + UNIT;
    if(length < 19) return df.format(energy/1000000000000000f) +" P" + UNIT;
    if(length < 22) return df.format(energy/1000000000000000000f) +" E" + UNIT;
    return "A bukkit load";
  }

}
