/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.events;

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import java.util.Objects;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.sentry.LoggedPluginManager;

public class Events {

  private Events() {
    throw new IllegalStateException("Utils Class");
  }

  private static LoggedPluginManager loggedPluginManager;

  public static void registerEvents(Listener listener) {
    if (Objects.isNull(loggedPluginManager)) {
      loggedPluginManager = new LoggedPluginManager(Craftory.plugin) {
        @Override
        protected void customHandler(Event event, Throwable e) {
          sentryLog(e);
        }
      };
    }
    loggedPluginManager.registerEvents(listener, Craftory.plugin);
  }

}
