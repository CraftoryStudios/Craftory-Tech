package tech.brettsaunders.craftory.api.logging.adapters;

import io.sentry.event.EventBuilder;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.logging.log4j.core.LogEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tech.brettsaunders.craftory.Craftory;

public class PluginInfo extends CraftLoggingAdapters {

  @Override
  public void processEvent(EventBuilder builder, LogEvent event) {
    builder.withExtra("Plugins", getLoadedPlugins());
  }

  private SortedMap<String, String> getLoadedPlugins() {
    SortedMap<String, String> pluginVersions = new TreeMap<>();
    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      pluginVersions.put(plugin.getName(), plugin.getDescription().getVersion());
    }
    return pluginVersions;
  }

}
