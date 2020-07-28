/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
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
  PLACER('\u1a0b');
  public final char label;

  Font(char c) {
    this.label = c;
  }
}
