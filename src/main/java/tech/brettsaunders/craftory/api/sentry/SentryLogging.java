package tech.brettsaunders.craftory.api.sentry;

import io.sentry.Sentry;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import io.sentry.event.interfaces.SentryException;
import java.util.Date;
import java.util.Deque;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SentryLogging {

  private SentryLogging() {
    throw new IllegalStateException("Utils Class");
  }

  public static void sentryLog(Throwable e) {
    Bukkit.getLogger().log(Level.SEVERE, e.getMessage(),e);
    EventBuilder eventBuilder = new EventBuilder()
        .withTimestamp(new Date(System.currentTimeMillis()))
        .withMessage(e.getMessage())
        .withLevel(io.sentry.event.Event.Level.ERROR);

    Deque<SentryException> exceptionDeque = SentryException.extractExceptionQueue(e);
    if(!exceptionDeque.isEmpty()) {
      SentryException firstException = exceptionDeque.removeFirst();
      if(firstException != null) {
        // If message in exception is empty, use the log message
        String exceptionMessage = firstException.getExceptionMessage();
        if(exceptionMessage == null || exceptionMessage.isEmpty()) {
          exceptionMessage = e.getMessage();
        }
        firstException = new SentryException(
            exceptionMessage,
            firstException.getExceptionClassName(),
            firstException.getExceptionPackageName(),
            firstException.getStackTraceInterface()
        );
        exceptionDeque.addFirst(firstException);
      }
    }
    eventBuilder.withSentryInterface(new ExceptionInterface(exceptionDeque));

    eventBuilder.withTag("Bukkit", Bukkit.getBukkitVersion());
    eventBuilder.withTag("CraftBukkit", Bukkit.getVersion());
    eventBuilder.withExtra("Online players", Bukkit.getOnlinePlayers().size());
    eventBuilder.withExtra("Plugins", getLoadedPlugins());
    eventBuilder.withTag("OS", System.getProperty("os.name"));
    eventBuilder.withTag("OS Cores",Runtime.getRuntime().availableProcessors() + "");
    eventBuilder.withTag("JavaVersion",System.getProperty("java.version"));

    Sentry.capture(eventBuilder);
  }

  private static SortedMap<String, String> getLoadedPlugins() {
    SortedMap<String, String> pluginVersions = new TreeMap<>();
    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      pluginVersions.put(plugin.getName(), plugin.getDescription().getVersion());
    }
    return pluginVersions;
  }

}
