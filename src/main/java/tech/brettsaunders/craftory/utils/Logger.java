/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class Logger {

  static final ChatColor INFO_COLOR = ChatColor.GREEN;
  static final ChatColor ERROR_COLOR = ChatColor.RED;
  static final ChatColor DEBUG_COLOR = ChatColor.AQUA;
  static final String prefix = "[" + Craftory.plugin.getDescription().getPrefix() + "] ";
  static final String debugPrefix =
      "[" + Craftory.plugin.getDescription().getPrefix() + " Debug] ";
  static final boolean debugMode = Utilities.config.getBoolean("general.debug");

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
    if (debugMode) {
      Bukkit.getLogger().info(debugPrefix + DEBUG_COLOR + logMessage);
    }
  }

}
