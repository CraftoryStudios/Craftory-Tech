/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
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
import tech.brettsaunders.craftory.Utilities;

public class Command_Help implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 1) {
      Utilities.msg(sender, Utilities.getTranslation("HelpCommandLineOne"));
      Utilities.msg(sender, Utilities.getTranslation("HelpCommandLineTwo"));
      Utilities.msg(sender, Utilities.getTranslation("HelpCommandLineThree"));
      Utilities.msg(sender, Utilities.getTranslation("HelpCommandLineFour"));
      Utilities.msg(sender, Utilities.getTranslation("HelpCommandLineFive"));

      Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder()
          .setCategory("command")
          .setTimestamp(new Date(System.currentTimeMillis()))
          .setMessage("Player "+sender.getName() + " used help command")
          .setType(Type.DEFAULT)
          .build());
    }
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    return CommandWrapper.filterTabs(tabs, args);
  }
}
