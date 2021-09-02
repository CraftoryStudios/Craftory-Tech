/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tech.brettsaunders.craftory.Utilities;

public class CommandWrapper implements CommandExecutor, TabCompleter {

  public static final String NO_PERMISSIONS = "NoPermissions";
  /* Commands */
  private final CommandExecutor mainCommand;
  private final CommandExecutor helpCommand;
  private final CommandExecutor debugCommand;
  private final CommandExecutor giveCommand;
  private final CommandExecutor recipeBookCommand;

  /* Tab Complete */
  private final TabCompleter mainTab;
  private final TabCompleter helpTab;
  private final TabCompleter debugTab;
  private final TabCompleter giveTab;
  private final TabCompleter recipeBookTab;

  public CommandWrapper() {
    /* Commands */
    mainCommand = new CommandMain();
    helpCommand = new CommandHelp();
    debugCommand = new CommandDebug();
    giveCommand = new CommandGiveItem();
    recipeBookCommand = new CommandRecipeBook();

    /* Tab Complete */
    mainTab = new CommandMain();
    helpTab = new CommandHelp();
    debugTab = new CommandDebug();
    giveTab = new CommandGiveItem();
    recipeBookTab = new CommandRecipeBook();
  }

  public static List<String> filterTabs(List<String> list, String[] origArgs) {
    if (origArgs.length == 0) {
      return list;
    }
    Iterator<String> itel = list.iterator();
    String label = origArgs[origArgs.length - 1].toLowerCase(Locale.ROOT);
    while (itel.hasNext()) {
      String name = itel.next();
      if (name.toLowerCase(Locale.ROOT).startsWith(label)) {
        continue;
      }
      itel.remove();
    }
    return list;
  }

  public static String[] getArgs(String[] args) {
    ArrayList<String> newArgs = new ArrayList<>();
    for (int i = 0; i < args.length - 1; i++) {
      String s = args[i];
      if (s.trim().isEmpty()) {
        continue;
      }
      newArgs.add(s);
    }
    return newArgs.toArray(new String[0]);
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (command.getName().equalsIgnoreCase("craftory") || command.getName()
        .equalsIgnoreCase("cr")) {
      if (args.length == 0) {
        return mainCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("help")) {
        if (sender.hasPermission("carftory.command.help")) {
          return helpCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, Utilities.getTranslation(NO_PERMISSIONS));
        }
      } else if (args[0].equalsIgnoreCase("toggleDebug")) {
        if (sender.hasPermission("craftory.command.debug")) {
          return debugCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, Utilities.getTranslation(NO_PERMISSIONS));
        }
      } else if (args[0].equalsIgnoreCase("recipebook")) {
        if (sender.hasPermission("craftory.command.recipebook")) {
          return recipeBookCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, Utilities.getTranslation(NO_PERMISSIONS));
        }
      } else if (args[0].equalsIgnoreCase("give")) {
        if (sender.hasPermission("craftory.command.give")) {
          return giveCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, Utilities.getTranslation(NO_PERMISSIONS));
        }
      }
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(final CommandSender sender, final Command command,
      final String label, final String[] args) {
    if (command.getName().equalsIgnoreCase("craftory") || command.getName()
        .equalsIgnoreCase("cr")) {
      if (args.length == 1) {
        if (sender.hasPermission("carftory.help")) {
          return mainTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("help")) {
        if (sender.hasPermission("carftory.help")) {
          return helpTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("toggleDebug")) {
        if (sender.hasPermission("craftory.debug")) {
          return debugTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("recipebook")) {
        if (sender.hasPermission("craftory.recipebook")) {
          return recipeBookTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("give")) {
        if (sender.hasPermission("craftory.give")) {
          return giveTab.onTabComplete(sender, command, label, args);
        }
      }
    }
    return Collections.emptyList();
  }
}
