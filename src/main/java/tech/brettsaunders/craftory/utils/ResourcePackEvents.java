/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import java.math.BigInteger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.events.Events;

public class ResourcePackEvents implements Listener {

  private final String RESOURCE_PACK = "https://download.mc-packs.net/pack/f539edb7419b4aa0a4a3d4c29e2a9e23a6f82d4a.zip";
  private final String HASH = "f539edb7419b4aa0a4a3d4c29e2a9e23a6f82d4a";

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
    e.getPlayer().setResourcePack(RESOURCE_PACK, hexStringToByteArray(HASH));
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
