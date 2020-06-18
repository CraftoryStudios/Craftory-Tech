package tech.brettsaunders.craftory.api.font;

public enum Font {
  FOUNDRY_GUI('\ue00a'),
  CELL_GUI('\ue000'),
  FURNACE_GUI('\ue009'),
  GENERATOR_GUI('\ue008');

  public final char label;

  Font(char c) {
    this.label = c;
  }
}
