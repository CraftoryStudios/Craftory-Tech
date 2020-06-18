package tech.brettsaunders.craftory.utils;

import java.math.BigInteger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import tech.brettsaunders.craftory.Craftory;

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
        e.getPlayer().sendMessage("Craftory: Downloading texture pack!");
        break;
      case DECLINED:
        e.getPlayer()
            .sendMessage("Craftory: You will not be able to see custom blocks, items and GUI's!!");
        break;
      case FAILED_DOWNLOAD:
        e.getPlayer().sendMessage("Craftory: Texture pack download failed... re-trying");
        break;
      case SUCCESSFULLY_LOADED:
        e.getPlayer().sendMessage("Craftory: Custom textures now enabled!");
    }
  }
}
