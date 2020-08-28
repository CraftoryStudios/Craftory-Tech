package tech.brettsaunders.craftory.api.advancments;

public enum Frame {
  TASK,
  CHALLENGE,
  GOAL;

  /**
   * @return a {@link String} representation of the enum value, which can be used in JSON objects
   */
  public String getValue() {
    return name().toLowerCase();
  }
}
