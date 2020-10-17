/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
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
  PLACER('\u1a0b'),
  DRIVE_VIEWER('\u1a11');
  public final char label;

  Font(char c) {
    this.label = c;
  }
}
