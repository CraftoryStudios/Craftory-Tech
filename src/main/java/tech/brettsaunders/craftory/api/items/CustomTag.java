package tech.brettsaunders.craftory.api.items;

import java.util.Arrays;
import java.util.List;

public enum CustomTag {
  WATERWHEEL(new String[]{"waterwheel", "waterwheel_oak", "waterwheel_birch", "waterwheel_acacia",
      "waterwheel_crimson", "waterwheel_jungle", "waterwheel_spruce", "waterwheel_warpped"});

  public final List<String> items;

  private CustomTag(String[] items) {
    this.items = Arrays.asList(items);
  }
}
