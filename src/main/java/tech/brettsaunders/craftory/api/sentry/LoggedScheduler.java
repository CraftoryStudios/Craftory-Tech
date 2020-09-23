package tech.brettsaunders.craftory.api.sentry;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

/**
 * Implements a delegating scheduler that automatically handles all exceptions.
 * @author Kristian
 */
public abstract class LoggedScheduler implements BukkitScheduler {

  private class TaskedRunnable implements Runnable {

    private int taskID = -1;
    private Runnable delegate;

    public TaskedRunnable(Runnable delegate) {
      this.delegate = delegate;
    }

    @Override
    public void run() {
      try {
        delegate.run();
      } catch (Throwable e) {
        customHandler(taskID, e);
      }
    }

    public int getTaskID() {
      return taskID;
    }

    public void setTaskID(int taskID) {
      this.taskID = taskID;
    }
  }

  // A reference to the underlying scheduler
  private BukkitScheduler delegate;

  public LoggedScheduler(Plugin owner) {
    this(owner.getServer().getScheduler());
  }

  public LoggedScheduler(BukkitScheduler delegate) {
    this.delegate = delegate;
  }

  /**
   * Invoked when an error occurs in a task.
   * @param taskID - unique ID of the task, or
   * @param e - error that occured.
   */
  protected abstract void customHandler(int taskID, Throwable e);

  @Override
  public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
    return delegate.callSyncMethod(plugin, task);
  }

  @Override
  public void cancelTask(int taskId) {
    delegate.cancelTask(taskId);
  }

  @Override
  public void cancelTasks(Plugin plugin) {
    delegate.cancelTasks(plugin);
  }

  @Override
  public List<BukkitWorker> getActiveWorkers() {
    return delegate.getActiveWorkers();
  }

  @Override
  public List<BukkitTask> getPendingTasks() {
    return delegate.getPendingTasks();
  }

  @Override
  public boolean isCurrentlyRunning(int taskId) {
    return delegate.isCurrentlyRunning(taskId);
  }

  @Override
  public boolean isQueued(int taskId) {
    return delegate.isQueued(taskId);
  }

  @Override
  public BukkitTask runTask(Plugin plugin, Runnable task) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTask(plugin, wrapped);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }
  @Override
  public void runTask(Plugin plugin, Consumer<BukkitTask> task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTask(Plugin plugin, BukkitRunnable task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable task) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTaskAsynchronously(plugin, wrapped);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }

  @Override
  public void runTaskAsynchronously(Plugin plugin, Consumer<BukkitTask> task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskLater(Plugin plugin, Runnable task, long delay) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTaskLater(plugin, wrapped, delay);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }

  @Override
  public void runTaskLater(Plugin plugin, Consumer<BukkitTask> task, long delay) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable task, long delay) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTaskLaterAsynchronously(plugin, wrapped, delay);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }

  @Override
  public void runTaskLaterAsynchronously(Plugin plugin, Consumer<BukkitTask> task, long delay) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable task, long delay) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTaskTimer(plugin, wrapped, delay, period);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }

  @Override
  public void runTaskTimer(Plugin plugin, Consumer<BukkitTask> task, long delay, long period) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable task, long delay, long period) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay,
      long period) {
    TaskedRunnable wrapped = new TaskedRunnable(task);
    BukkitTask bukkitTask = delegate.runTaskTimerAsynchronously(plugin, wrapped, delay, period);
    wrapped.setTaskID(bukkitTask.getTaskId());
    return bukkitTask;
  }

  @Override
  public void runTaskTimerAsynchronously(Plugin plugin, Consumer<BukkitTask> task, long delay,
      long period) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable task, long delay,
      long period) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleAsyncDelayedTask(plugin, wrapped));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleAsyncDelayedTask(plugin, wrapped, delay));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleAsyncRepeatingTask(plugin, wrapped, delay, period));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleSyncDelayedTask(plugin, wrapped));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleSyncDelayedTask(plugin, wrapped, delay));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
    TaskedRunnable wrapped = new TaskedRunnable(task);

    wrapped.setTaskID(delegate.scheduleSyncRepeatingTask(plugin, wrapped, delay, period));
    return wrapped.getTaskID();
  }

  @Override
  public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l,
      long l1) {
    throw new UnsupportedOperationException();
  }
}