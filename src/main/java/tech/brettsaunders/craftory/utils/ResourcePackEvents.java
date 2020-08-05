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
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class ResourcePackEvents implements Listener {

  public ResourcePackEvents() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
  }

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
  }

  @EventHandler
  public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
    switch (e.getStatus()) {
      case ACCEPTED:
        //e.getPlayer().sendMessage("Craftory: Downloading texture pack!");
        e.getPlayer().setInvulnerable(true);
        break;
      case DECLINED:
        e.getPlayer()
            .sendMessage(ChatColor.RED + "[Craftory]"+ChatColor.RESET+ Utilities.getTranslation("ResourcePackDeclined"));
        break;
      case FAILED_DOWNLOAD:
        e.getPlayer().sendMessage(ChatColor.RED + "[Craftory]"+ChatColor.RESET+Utilities.getTranslation("ResourcePackFailed"));
        e.getPlayer().setInvulnerable(false);
        break;
      case SUCCESSFULLY_LOADED:
        e.getPlayer().sendMessage(ChatColor.RED + "[Craftory]"+ChatColor.RESET+Utilities.getTranslation("ResourcePackEnabled"));
        e.getPlayer().setInvulnerable(false);
    }
  }
}
