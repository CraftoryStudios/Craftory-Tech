package tech.brettsaunders.craftory.api.logging;

import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import io.sentry.event.interfaces.MessageInterface;
import io.sentry.event.interfaces.SentryException;
import io.sentry.log4j2.SentryAppender;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import tech.brettsaunders.craftory.api.logging.adapters.CraftLoggingAdapters;
import tech.brettsaunders.craftory.api.logging.adapters.PluginInfo;
import tech.brettsaunders.craftory.api.logging.adapters.ServerInfo;
import tech.brettsaunders.craftory.api.logging.adapters.StackInfo;
import tech.brettsaunders.craftory.api.logging.filters.CraftoryFilter;
import tech.brettsaunders.craftory.utils.Log;

public class CraftSentryAppender extends SentryAppender {

  private Set<CraftLoggingAdapters> adapters;

  public CraftSentryAppender() {

    this.addFilter(new CraftoryFilter());

    adapters = new HashSet<>();
    this.adapters.add(new StackInfo());
    this.adapters.add(new PluginInfo());
    this.adapters.add(new ServerInfo());


  }

  @Override
  public void stop() {
    super.stop();
    for(CraftLoggingAdapters adapter : adapters) {
      adapter.shutdown();
    }
  }

  @Override
  public String getName() {
    return "CraftoryAppender";
  }

  public Event.Level levelToEventLevel(Level level) {
    if(level.equals(Level.WARN)) {
      return Event.Level.WARNING;
    } else if(level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
      return Event.Level.ERROR;
    } else if(level.equals(Level.DEBUG)) {
      return Event.Level.DEBUG;
    } else {
      return Event.Level.INFO;
    }
  }

  @Override
  protected EventBuilder createEventBuilder(LogEvent event) {
    // Basics
    Message eventMessage = event.getMessage();
    EventBuilder eventBuilder = new EventBuilder()
        .withSdkIntegration("log4j2")
        .withTimestamp(new Date(event.getTimeMillis()))
        .withMessage(eventMessage.getFormattedMessage())
        .withLogger(event.getLoggerName())
        .withLevel(levelToEventLevel(event.getLevel()))
        .withExtra(THREAD_NAME, event.getThreadName());

    // Message format (if message formatting is used)
    if(eventMessage.getFormat() != null
        && eventMessage.getFormattedMessage() != null
        && !eventMessage.getFormattedMessage().equals(eventMessage.getFormat())) {
      eventBuilder.withSentryInterface(new MessageInterface(
          eventMessage.getFormat(),
          formatMessageParameters(eventMessage.getParameters()),
          eventMessage.getFormattedMessage()));
    }

    // Exception
    Throwable throwable = event.getThrown();
    if(throwable != null) {
      Deque<SentryException> exceptionDeque = SentryException.extractExceptionQueue(throwable);
      if(!exceptionDeque.isEmpty()) {
        SentryException firstException = exceptionDeque.removeFirst();
        if(firstException != null) {
          // If message in exception is empty, use the log message
          String exceptionMessage = firstException.getExceptionMessage();
          if(exceptionMessage == null || exceptionMessage.isEmpty()) {
            exceptionMessage = eventMessage.getFormattedMessage();
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
    }

    // Culprit
    eventBuilder.withCulprit(event.getLoggerName());

    // Log4j metadata
    if(event.getContextStack() != null && !event.getContextStack().asList().isEmpty()) {
      eventBuilder.withExtra(LOG4J_NDC, event.getContextStack().asList());
    }

    // Log4j marker
    if(event.getMarker() != null) {
      eventBuilder.withTag(LOG4J_MARKER, event.getMarker().getName());
    }

    // Run EventEditors
    for(CraftLoggingAdapters adapter : adapters) {
      try {
        adapter.processEvent(eventBuilder, event);
      } catch(Exception e) {
        Log.error("EventEditor", adapter.getClass().getName(), "failed:",
            ExceptionUtils.getStackTrace(e));
      }
    }

    Log.debug("Sending event to sentry:", eventBuilder);
    return eventBuilder;
  }

}
