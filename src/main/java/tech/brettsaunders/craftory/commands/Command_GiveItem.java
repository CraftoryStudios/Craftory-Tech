package tech.brettsaunders.craftory.commands;

import java.util.ArrayList;
import java.util.Arrays;
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

public class Command_GiveItem implements CommandExecutor, TabCompleter {

  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (args.length == 3) {
      giveCustomItem(1, args[1], args[2], sender);
    } else if (args.length == 4) {
      int amount = 1;
      try {
        amount = Integer.parseInt(args[3]);
      } catch (NumberFormatException ignored) {
        Utilities.msg(sender,"Couldn't give block, amount not recognised");
      }
      if (amount > 64) amount = 64;
      giveCustomItem(amount, args[1], args[2], sender);
    } else {
      Utilities.msg(sender, "Usage: /cf give [Player] [ItemName] <[amount]>");
    }
    return true;
  }

  public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
    ArrayList<String> tabs = new ArrayList<>();
    if (args.length == 2) {
      return getOnlinePlayerNames();
    } else if (args.length == 3) {
      tabs.add("<item>");
    } else if (args.length == 4) {
      tabs.add("<amount>");
    }
    return CommandWrapper.filterTabs(tabs, args);
  }

  private List<String> getOnlinePlayerNames() {
    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
    Bukkit.getServer().getOnlinePlayers().toArray(players);
    List<String> playerNames = Arrays.stream(players).map(Player::getName)
        .collect(Collectors.toList());
    return playerNames;
  }

  private boolean giveCustomItem(int amount, String playerName, String itemName, CommandSender sender) {
    if (getOnlinePlayerNames().contains(playerName)) {
      ItemStack itemStack = CustomItemManager.getCustomItem(itemName, false);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        itemStack.setAmount(amount);
        Player player = Craftory.plugin.getServer().getPlayer(playerName);
        if (player != null) {
          int slot = player.getInventory().firstEmpty();
          if (slot == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            Utilities.msg(sender, "Gave " + playerName + " x" + amount + " " + itemName);
            return true;
          } else {
            player.getInventory().setItem(slot, itemStack);
            Utilities.msg(sender, "Gave " + playerName + " x" + amount + " " + itemName);
            return true;
          }
        } else {
          Utilities.msg(sender, "Couldn't give item, player doesn't exist");
        }
      } else {
        Utilities.msg(sender, "Couldn't give item, item isn't a Custom Item");
      }
    }
    return false;
  }
}
