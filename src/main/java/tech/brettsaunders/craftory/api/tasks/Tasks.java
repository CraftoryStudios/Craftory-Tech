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

import tech.brettsaunders.craftory.Craftory;

public class Tasks {

  public static void syncDelayedTask(Runnable runnable, long delay) {
    Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin, runnable,delay);
  }

  public static void runAsyncTask(Runnable runnable) {
    Craftory.plugin.getServer().getScheduler().runTaskAsynchronously(Craftory.plugin, runnable);
  }

  public static void runTaskLater(Runnable runnable, long delay) {
    Craftory.plugin.getServer().getScheduler().runTaskLater(Craftory.plugin, runnable, delay);
  }

  public static void runAsyncTaskLater(Runnable runnable, long delay) {
    Craftory.plugin.getServer().getScheduler().runTaskLaterAsynchronously(Craftory.plugin, runnable, delay);
  }

  public static void runTaskTimer(Runnable runnable, long delay, long period) {
    Craftory.plugin.getServer().getScheduler().runTaskTimer(Craftory.plugin, runnable, delay, period);
  }

  public static void runTaskTimer(Runnable runnable, long period) {
    runTaskTimer(runnable, 0, period);
  }

  public static void runAsyncTaskTimer(Runnable runnable, long delay, long period) {
    Craftory.plugin.getServer().getScheduler().runTaskTimerAsynchronously(Craftory.plugin, runnable, delay, period);
  }

  public static void runAsyncTaskTimer(Runnable runnable, long period) {
    runAsyncTaskTimer(runnable, 0, period);
  }
}
