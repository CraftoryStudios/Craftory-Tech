package tech.brettsaunders.craftory.api.font;

public enum NegativeSpaceFont {
  MINUS_8('\uF808'),
  MINUS_16('\uF809'),
  MINUS_32('\uF80A'),
  MINUS_128('\uF80C');


  public final char label;

  NegativeSpaceFont(char c) {
    this.label = c;
  }
}
