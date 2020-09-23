/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.events;

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import io.sentry.Sentry;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import io.sentry.event.interfaces.SentryException;
import java.util.Date;
import java.util.Deque;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.sentry.LoggedPluginManager;

public class Events {

  private static LoggedPluginManager events;

  public static void registerEvents(Listener listener) {
    if (Objects.isNull(events)) {
      events = new LoggedPluginManager(Craftory.plugin) {
        @Override
        protected void customHandler(Event event, Throwable e) {
          sentryLog(e);
        }
      };
    }
    events.registerEvents(listener, Craftory.plugin);
  }

}
