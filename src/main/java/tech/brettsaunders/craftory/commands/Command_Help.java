package tech.brettsaunders.craftory.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tech.brettsaunders.craftory.Utilities;

public class Command_Help implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 1) {
      Utilities.msg(sender, "&8/&acf help  &7-&f  Shows this list");
      Utilities.msg(sender, "&8/&acf toggleDebug  &7-&f  Toggles showing debug messages (OP Only)");
      Utilities.msg(sender, "&8/&acf give  &7-&f  Give player a custom item");
    } else {
      Utilities.msg(sender, "Usage");
    }
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    return CommandWrapper.filterTabs(tabs, args);
  }
}
