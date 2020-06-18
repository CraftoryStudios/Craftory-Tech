package tech.brettsaunders.craftory.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import tech.brettsaunders.craftory.Craftory;

public class ResourcePackEvents implements Listener {

  public ResourcePackEvents() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
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
        e.getPlayer().sendMessage("Craftory: You will not be able to see custom blocks, items and GUI's!!");
        break;
      case FAILED_DOWNLOAD:
        e.getPlayer().sendMessage("Craftory: Texture pack download failed... re-trying");
        e.getPlayer().setResourcePack(Craftory.RESOURCE_PACK, hexStringToByteArray(Craftory.HASH));
        break;
      case SUCCESSFULLY_LOADED:
        e.getPlayer().sendMessage("Craftory: Custom textures now enabled!");
    }
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }
}
