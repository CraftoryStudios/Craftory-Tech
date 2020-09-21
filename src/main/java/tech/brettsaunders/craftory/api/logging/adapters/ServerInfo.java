package tech.brettsaunders.craftory.api.logging.adapters;

import io.sentry.event.EventBuilder;
import org.apache.logging.log4j.core.LogEvent;
import org.bukkit.Bukkit;
import tech.brettsaunders.craftory.Craftory;

public class ServerInfo extends CraftLoggingAdapters {

  private String serverVersion;

  public ServerInfo() {
    serverVersion = Bukkit.getVersion();
  }

  @Override
  public void processEvent(EventBuilder builder, LogEvent event) {
    // Server information
    builder.withTag("API", serverVersion);
    builder.withExtra("Online players", Bukkit.getOnlinePlayers().size());
    builder.withServerName(Bukkit.getServer().getName());
    builder.withExtra("Bukkit", Bukkit.getBukkitVersion());
    builder.withExtra("CraftBukkit",Bukkit.getVersion());
    builder.withRelease(Craftory.VERSION);
  }
}
