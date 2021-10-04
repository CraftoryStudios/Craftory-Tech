/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class ResourcepackService implements Listener {
  private byte[] cachedHash;
  private String url = "";

  public ResourcepackService() {
    Events.registerEvents(this);
    url = "https://raw.githubusercontent.com/CraftoryStudios/Craftory-Tech/v" + Craftory.plugin.getDescription().getVersion() + "/resourcepacks/original"
        + ".zip";
    cachedHash = calcSHA1Hash(url);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    e.getPlayer().setResourcePack(url, cachedHash);

    if (!e.getPlayer().hasPlayedBefore() && Utilities.config.isBoolean("general.recipeBookOnFirstJoin")
        && Utilities.config.getBoolean("general.recipeBookOnFirstJoin")) {
      ItemStack recipeBook = CustomItemManager.getCustomItem("recipe_book");
      e.getPlayer().getInventory().addItem(recipeBook);
    }
  }

  @EventHandler
  public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
    if (e.getStatus() == Status.ACCEPTED) {
      e.getPlayer().setInvulnerable(true);
      return;
    }
    e.getPlayer().setInvulnerable(false);
  }

  private byte[] calcSHA1Hash(String value) {
    try {
      URL url = new URL(value);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      if (connection.getContentLength() <= 0) {
        return null;
      }
      byte[] resourcePackBytes = new byte[connection.getContentLength()];
      InputStream in = connection.getInputStream();

      int b;
      int i = 0;
      while ((b = in.read()) != -1) {
        resourcePackBytes[i] = (byte) b;
        i++;
      }

      in.close();

      MessageDigest md = MessageDigest.getInstance("SHA-1");
      return md.digest(resourcePackBytes);
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    } catch (Exception ignored) {
    }
    return null;
  }
}
