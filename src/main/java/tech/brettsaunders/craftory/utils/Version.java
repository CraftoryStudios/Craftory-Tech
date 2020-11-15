package tech.brettsaunders.craftory.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Server;

public class Version implements Comparable<Version>{
  private String version;
  private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-.]+).*");


  public final String get() {
    return this.version;
  }

  public Version(String version) {
    if(version == null)
      throw new IllegalArgumentException("Version can not be null");
    if(!version.matches("[0-9]+(\\.[0-9]+)*"))
      throw new IllegalArgumentException("Invalid version format");
    this.version = version;
  }

  public Version(Server server) {
    this(extractVersion(server.getVersion()));
  }

  public static String extractVersion(String text) {
    Matcher version = VERSION_PATTERN.matcher(text);

    if (version.matches() && version.group(1) != null) {
      return version.group(1);
    } else {
      throw new IllegalStateException("Cannot parse version String '" + text + "'");
    }
  }

  @Override public int compareTo(Version that) {
    if(that == null)
      return 1;
    String[] thisParts = this.get().split("\\.");
    String[] thatParts = that.get().split("\\.");
    int length = Math.max(thisParts.length, thatParts.length);
    for(int i = 0; i < length; i++) {
      int thisPart = i < thisParts.length ?
          Integer.parseInt(thisParts[i]) : 0;
      int thatPart = i < thatParts.length ?
          Integer.parseInt(thatParts[i]) : 0;
      if(thisPart < thatPart)
        return -1;
      if(thisPart > thatPart)
        return 1;
    }
    return 0;
  }

  @Override public boolean equals(Object that) {
    if(this == that)
      return true;
    if(that == null)
      return false;
    if(this.getClass() != that.getClass())
      return false;
    return this.compareTo((Version) that) == 0;
  }
}
