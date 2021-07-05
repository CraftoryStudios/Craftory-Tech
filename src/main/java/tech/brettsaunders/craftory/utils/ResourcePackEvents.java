/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import java.math.BigInteger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class ResourcePackEvents implements Listener {

  public ResourcePackEvents() {
    Events.registerEvents(this);
  }

  private static final String CRAFTORY_MESSAGE_PREFIX = "[Craftory]";

  public static byte[] hexStringToByteArray(String s) {
    byte[] byteArray = new BigInteger(s, 16).toByteArray();
    if (byteArray[0] == 0) {
      byte[] output = new byte[byteArray.length - 1];
      System.arraycopy(byteArray, 1, output, 0, output.length);
      return output;
    }
    return byteArray;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    e.getPlayer().setResourcePack(Craftory.RESOURCE_PACK, hexStringToByteArray(Craftory.HASH));
    if (!e.getPlayer().hasPlayedBefore() && Utilities.config.isBoolean("general.recipeBookOnFirstJoin")
        && Utilities.config.getBoolean("general.recipeBookOnFirstJoin")) {
      ItemStack recipeBook = CustomItemManager.getCustomItem("recipe_book");
      e.getPlayer().getInventory().addItem(recipeBook);
    }
  }

  @EventHandler
  public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
    switch (e.getStatus()) {
      case ACCEPTED:
        e.getPlayer().setInvulnerable(true);
        break;
      default:
      case DECLINED:
        e.getPlayer()
            .sendMessage(ChatColor.RED + CRAFTORY_MESSAGE_PREFIX + ChatColor.RESET + Utilities
                .getTranslation("ResourcePackDeclined"));
        break;
      case FAILED_DOWNLOAD:
        e.getPlayer().sendMessage(ChatColor.RED + CRAFTORY_MESSAGE_PREFIX + ChatColor.RESET + Utilities
            .getTranslation("ResourcePackFailed"));
        e.getPlayer().setInvulnerable(false);
        break;
      case SUCCESSFULLY_LOADED:
        e.getPlayer().sendMessage(ChatColor.RED + CRAFTORY_MESSAGE_PREFIX + ChatColor.RESET + Utilities
            .getTranslation("ResourcePackEnabled"));
        e.getPlayer().setInvulnerable(false);
    }
  }
}
