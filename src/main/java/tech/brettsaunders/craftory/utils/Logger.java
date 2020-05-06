package tech.brettsaunders.craftory.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tech.brettsaunders.craftory.Craftory;

public class Logger {
  static String prefix = "[" + Craftory.getInstance().getDescription().getPrefix() + "] ";
  static String debugPrefix = "[" + Craftory.getInstance().getDescription().getPrefix() + " Debug] ";
  static final ChatColor INFO_COLOR = ChatColor.GREEN;
  static final ChatColor ERROR_COLOR = ChatColor.RED;
  static final ChatColor DEBUG_COLOR = ChatColor.AQUA;
  static boolean debugMode = Craftory.getDebugMode();

  public static void info(String logMessage) {
    Bukkit.getLogger().info(prefix + INFO_COLOR + logMessage);
  }

  public static void infoDiscrete(String logMessage) {
    Bukkit.getLogger().info(prefix + logMessage);
  }

  public static void warn(String logMessage) {
    Bukkit.getLogger().warning(prefix + logMessage);
  }

  public static void error(String logMessage) {
    Bukkit.getLogger().warning(ERROR_COLOR + prefix + logMessage);
  }

  public static void debug(String logMessage) {
    if (debugMode) Bukkit.getLogger().info( debugPrefix + DEBUG_COLOR + logMessage);
  }

}
