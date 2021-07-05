/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.commands;

import io.sentry.Sentry;
import io.sentry.event.Breadcrumb.Type;
import io.sentry.event.BreadcrumbBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class CommandGiveItem implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 3) {
      giveCustomItem(1, args[1], args[2], sender);
    } else if (args.length == 4) {
      int amount = 1;
      try {
        amount = Integer.parseInt(args[3]);
      } catch (NumberFormatException ignored) {
        Utilities.msg(sender, Utilities.getTranslation("GiveCommandErrorAmount"));
      }
      if (amount > 64) {
        amount = 64;
      }
      giveCustomItem(amount, args[1], args[2], sender);

      Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder()
          .setCategory("command")
          .setTimestamp(new Date(System.currentTimeMillis()))
          .setMessage("Player "+sender.getName() + " used give item command, spawn "+ args[2])
          .setType(Type.DEFAULT)
          .build());
    } else {
      Utilities.msg(sender, Utilities.getTranslation("GiveCommandUsage"));
    }
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    if (args.length == 2) {
      return getOnlinePlayerNames();
    } else if (args.length == 3) {
      if (args[2].isEmpty()) {
        return CustomItemManager.getItemNames();
      } else {
        return CustomItemManager.getItemNames().stream().filter(name -> name.startsWith(args[2]))
            .collect(
                Collectors.toList());
      }
    } else if (args.length == 4) {
      tabs.add("<amount>");
    }
    return CommandWrapper.filterTabs(tabs, args);
  }

  private List<String> getOnlinePlayerNames() {
    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
    Bukkit.getServer().getOnlinePlayers().toArray(players);
    return Arrays.stream(players).map(Player::getName)
        .collect(Collectors.toList());
  }

  private boolean giveCustomItem(int amount, String playerName, String itemName,
      CommandSender sender) {
    if (getOnlinePlayerNames().contains(playerName)) {
      ItemStack itemStack = CustomItemManager.getCustomItem(itemName);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        itemStack.setAmount(amount);
        Player player = Craftory.plugin.getServer().getPlayer(playerName);
        if (player != null) {
          HashMap<Integer, ItemStack> result = player.getInventory().addItem(itemStack);
          if (result.size() > 0) {
            result.forEach(
                (i, item) -> player.getWorld().dropItemNaturally(player.getLocation(), item));
          }
          Utilities.msg(sender,
              Utilities.getTranslation("GiveCommandGave") + " " + playerName + " x" + amount + " "
                  + itemName);
          return true;
        } else {
          Utilities.msg(sender, Utilities.getTranslation("GiveCommandErrorPlayer"));
        }
      } else {
        Utilities.msg(sender, Utilities.getTranslation("GiveCommandErrorNotItem"));
      }
    }
    return false;
  }
}
