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
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.recipes.RecipeBook;

public class CommandRecipeBook implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 1) {
      RecipeBook.openRecipeBook((Player)sender);
      if (Utilities.isSentryEnabled()) {
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder()
                .setCategory("command")
                .setTimestamp(new Date(System.currentTimeMillis()))
                .setMessage("Player " + sender.getName() + " used recipe book")
                .setType(Type.DEFAULT)
                .build());
      }
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
