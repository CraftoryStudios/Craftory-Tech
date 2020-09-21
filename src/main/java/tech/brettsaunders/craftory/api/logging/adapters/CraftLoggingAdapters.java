package tech.brettsaunders.craftory.api.logging.adapters;

import io.sentry.event.EventBuilder;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.LogEvent;

@NoArgsConstructor
public abstract class CraftLoggingAdapters {

  public abstract void processEvent(EventBuilder builder, LogEvent event);

  public void shutdown() { }

}
