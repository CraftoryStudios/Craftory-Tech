package tech.brettsaunders.craftory.api.sentry;

import io.sentry.Sentry;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import io.sentry.event.interfaces.SentryException;
import java.util.Date;
import java.util.Deque;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class SentryLogging {

  public static void sentryLog(Throwable e) {
    Bukkit.getLogger().log(Level.SEVERE, e.getMessage(),e);
    EventBuilder eventBuilder = new EventBuilder()
        .withTimestamp(new Date(System.currentTimeMillis()))
        .withMessage(e.getMessage())
        .withLevel(io.sentry.event.Event.Level.ERROR);

    if(e != null) {
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
    }

    Sentry.capture(eventBuilder);
  }

}
