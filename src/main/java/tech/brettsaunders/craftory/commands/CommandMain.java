/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.commands;

import io.sentry.Sentry;
import io.sentry.event.Breadcrumb.Type;
import io.sentry.event.BreadcrumbBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class CommandMain implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    Utilities.msg(sender, Utilities.getTranslation("MainCommandLineOne") + Craftory.plugin.getDescription().getVersion());
    Utilities.msg(sender, Utilities.getTranslation("MainCommandLineTwo") + " ©");
    Utilities.msg(sender, "Reporting ID: "+Utilities.data.getString("reporting.serverUUID"));

    if (Utilities.isSentryEnabled())
      Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder()
          .setCategory("command")
          .setTimestamp(new Date(System.currentTimeMillis()))
          .setMessage("Player "+sender.getName() + " used main command")
          .setType(Type.DEFAULT)
          .build());
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    tabs.add("help");
    tabs.add("toggleDebug");
    tabs.add("give");
    tabs.add("fixGraphics");
    tabs.add("recipeBook");
    return CommandWrapper.filterTabs(tabs, args);
  }
}
