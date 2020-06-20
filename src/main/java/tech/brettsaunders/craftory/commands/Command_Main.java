package tech.brettsaunders.craftory.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class Command_Main implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    Utilities.msg(sender, "[Craftory] &fRunning &9v" + Craftory.VERSION);
    Utilities.msg(sender, "[Craftory] &fMade by &6 Brett Saunders & Matty Jones &f, © ");
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    tabs.add("help");
    tabs.add("toggleDebug");
    tabs.add("give");
    return CommandWrapper.filterTabs(tabs, args);
  }
}
