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
      loggedPluginManager = new LoggedPluginManager(Craftory.instance) {
        @Override
        protected void customHandler(Event event, Throwable e) {
          sentryLog(e);
        }
      };
    }
    loggedPluginManager.registerEvents(listener, Craftory.instance);
  }

}
