/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.font;

public enum Font {
  ELECTRIC_FOUNDRY_GUI('\u1a0d'),
  CELL_GUI('\u1a00'),
  FURNACE_GUI('\u1a09'),
  IRON_FOUNDRY_GUI('\u1a0e'),
  GENERATOR_GUI('\u1a08'),
  GEOTHERMAL_GUI('\u1a0a'),
  BLANK('\u1a0c'),
  BOOK('\u1a0f'),
  SOLAR('\u1a10'),
  PLACER('\u1a0b');
  public final char label;

  Font(char c) {
    this.label = c;
  }
}
