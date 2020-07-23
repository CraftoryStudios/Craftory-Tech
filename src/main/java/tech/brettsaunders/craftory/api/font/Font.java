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
  ELECTRIC_FOUNDRY_GUI('\ue00d'),
  CELL_GUI('\ue000'),
  FURNACE_GUI('\ue009'),
  IRON_FOUNDRY_GUI('\ue009'),
  GENERATOR_GUI('\ue008'),
  GEOTHERMAL_GUI('\ue00A'),
  BLANK('\ue00C'),
  PLACER('\ue00B');
  public final char label;

  Font(char c) {
    this.label = c;
  }
}
