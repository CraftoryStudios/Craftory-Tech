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
      Utilities.msg(sender, Utilities.langProperties.getProperty("HelpCommandLineOne"));
      Utilities.msg(sender, Utilities.langProperties.getProperty("HelpCommandLineTwo"));
      Utilities.msg(sender, Utilities.langProperties.getProperty("HelpCommandLineThree"));
    }
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    return CommandWrapper.filterTabs(tabs, args);
  }
}
