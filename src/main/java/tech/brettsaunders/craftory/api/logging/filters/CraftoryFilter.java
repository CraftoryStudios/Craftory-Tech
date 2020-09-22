package tech.brettsaunders.craftory.api.logging.filters;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import tech.brettsaunders.craftory.utils.Log;

public class CraftoryFilter extends AbstractFilter {

  private Result filter(Level level, String msg) {
    if ((level.equals(Level.ERROR) || level.equals(Level.FATAL) || level.equals(Level.WARN)) && !msg.trim().startsWith("at")) {
        return Result.NEUTRAL;
    }
    return Result.DENY;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
    return filter(level, msg);
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
    return filter(level, msg.toString());
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
    return filter(level, msg.getFormattedMessage());
  }

  @Override
  public Result filter(LogEvent event) {
    return filter(event.getLevel(), event.getMessage().getFormattedMessage());
  }

}
