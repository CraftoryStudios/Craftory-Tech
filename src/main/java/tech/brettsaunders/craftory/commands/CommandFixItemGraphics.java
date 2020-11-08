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
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class CommandFixItemGraphics implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 1 && sender instanceof Player) {
        Player player = (Player) sender;
        CustomItemManager.updateInventoryItemGraphics(player.getInventory());
        CustomItemManager.updateInventoryItemGraphics(player.getEnderChest());
        Utilities.msg(sender, "Update graphics of custom items in inventory.");
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder()
            .setCategory("command")
            .setTimestamp(new Date(System.currentTimeMillis()))
            .setMessage("Player "+sender.getName() + " used fix item Graphics command")
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
