/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.tasks;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.sentry.LoggedScheduler;

public class Tasks {

  private static LoggedScheduler scheduler;

  private static LoggedScheduler getScheduler() {
    if (Objects.isNull(scheduler)) {
      scheduler = new LoggedScheduler(Craftory.plugin) {
        @Override
        protected void customHandler(int taskID, Throwable e) {
          sentryLog(e);
        }
      };
    }
    return scheduler;
  }

  public static void syncDelayedTask(Runnable runnable, long delay) {
    getScheduler().scheduleSyncDelayedTask(Craftory.plugin, runnable,delay);
  }

  public static void runAsyncTask(Runnable runnable) {
    getScheduler().runTaskAsynchronously(Craftory.plugin, runnable);
  }

  public static void runTaskLater(Runnable runnable, long delay) {
    getScheduler().runTaskLater(Craftory.plugin, runnable, delay);
  }

  public static void runAsyncTaskLater(Runnable runnable, long delay) {
    getScheduler().runTaskLaterAsynchronously(Craftory.plugin, runnable, delay);
  }

  public static void runTaskTimer(Runnable runnable, long delay, long period) {
    getScheduler().runTaskTimer(Craftory.plugin, runnable, delay, period);
  }

  public static void runTaskTimer(Runnable runnable, long period) {
    runTaskTimer(runnable, 0, period);
  }

  public static void runAsyncTaskTimer(Runnable runnable, long delay, long period) {
    getScheduler().runTaskTimerAsynchronously(Craftory.plugin, runnable, delay, period);
  }

  public static void runAsyncTaskTimer(Runnable runnable, long period) {
    runAsyncTaskTimer(runnable, 0, period);
  }
}
