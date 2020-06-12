package tech.brettsaunders.craftory.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tech.brettsaunders.craftory.Utilities;

public class CommandWrapper implements CommandExecutor, TabCompleter {

  /* Commands */
  private CommandExecutor MainCommand;
  private CommandExecutor HelpCommand;
  private CommandExecutor DebugCommand;

  /* Tab Complete */
  private TabCompleter MainTab;
  private TabCompleter HelpTab;
  private TabCompleter DebugTab;

  public CommandWrapper() {
    /* Commands */
    MainCommand = new Command_Main();
    HelpCommand = new Command_Help();
    DebugCommand = new Command_Debug();

    /* Tab Complete */
    MainTab = new Command_Main();
    HelpTab = new Command_Help();
    DebugTab = new Command_Debug();
  }

  public static ArrayList<String> filterTabs(ArrayList<String> list, String[] origArgs) {
    if (origArgs.length == 0) {
      return list;
    }
    Iterator<String> itel = list.iterator();
    String label = origArgs[origArgs.length - 1].toLowerCase();
    while (itel.hasNext()) {
      String name = itel.next();
      if (name.toLowerCase().startsWith(label)) {
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
        .equalsIgnoreCase("cf")) {
      if (args.length == 0) {
        return MainCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("help")) {
        if (sender.hasPermission("carftory.help")) {
          return HelpCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, "You Don't have Permissions for that");
        }
      } else if (args[0].equalsIgnoreCase("toggleDebug")) {
        if (sender.hasPermission("craftory.debug")) {
          return DebugCommand.onCommand(sender, command, label, args);
        } else {
          Utilities.msg(sender, "You Don't have Permissions for that");
        }
      }
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(final CommandSender sender, final Command command,
      final String label, final String[] args) {
    if (command.getName().equalsIgnoreCase("craftory") || command.getName()
        .equalsIgnoreCase("cf")) {
      if (args.length == 1) {
        if (sender.hasPermission("carftory.help")) {
          return MainTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("help")) {
        if (sender.hasPermission("carftory.help")) {
          return HelpTab.onTabComplete(sender, command, label, args);
        }
      } else if (args[0].equalsIgnoreCase("toggleDebug")) {
        if (sender.hasPermission("craftory.debug")) {
          return DebugTab.onTabComplete(sender, command, label, args);
        }
      }
    }
    return null;
  }
}
