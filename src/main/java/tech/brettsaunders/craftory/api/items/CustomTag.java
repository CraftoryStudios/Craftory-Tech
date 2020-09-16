package tech.brettsaunders.craftory.api.items;

import java.util.Arrays;
import java.util.List;

public enum CustomTag {
  WATERWHEEL(new String[]{"waterwheel", "waterwheel_oak", "waterwheel_birch", "waterwheel_acacia",
      "waterwheel_crimson", "waterwheel_jungle", "waterwheel_spruce", "waterwheel_warpped"}),
  WINDMILL(new String[]{"windmill","windmill_orange","windmill_black","windmill_blue",
      "windmill_brown","windmill_cyan","windmill_gray","windmill_green","windmill_light_blue",
      "windmill_light_gray","windmill_lime","windmill_magenta","windmill_pink","windmill_purple",
      "windmill_red","windmill_yellow"});

  public final List<String> items;

  private CustomTag(String[] items) {
    this.items = Arrays.asList(items);
  }
}
