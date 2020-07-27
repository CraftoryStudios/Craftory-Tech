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

public enum NegativeSpaceFont {
  MINUS_1('\uF801'),
  MINUS_2('\uF802'),
  MINUS_3('\uF803'),
  MINUS_4('\uF804'),
  MINUS_5('\uF805'),
  MINUS_6('\uF806'),
  MINUS_7('\uF807'),
  MINUS_8('\uF808'),
  MINUS_16('\uF809'),
  MINUS_32('\uF80A'),
  MINUS_64('\uF80B'),
  MINUS_128('\uF80C'),
  MINUS_256('\uF80C'),
  MINUS_512('\uF80C'),
  MINUS_1024('\uF80C'),
  PLUS_32('\uF80A');


  public final char label;

  NegativeSpaceFont(char c) {
    this.label = c;
  }
}
