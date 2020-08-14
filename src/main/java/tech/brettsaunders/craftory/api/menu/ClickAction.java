/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.menu;

/**
 * This simple enum represents four types of Clicks:
 *
 * LEFT_CLICK,
 * SHIFT_LEFT_CLICK,
 * RIGHT_CLICK,
 * SHIFT_RIGHT_CLICK
 *
 * @author TheBusyBiscuit
 *
 */
public enum ClickAction {

  LEFT_CLICK,
  SHIFT_LEFT_CLICK,
  RIGHT_CLICK,
  SHIFT_RIGHT_CLICK;

  public boolean isRightClick() {
    return equals(RIGHT_CLICK) || equals(SHIFT_RIGHT_CLICK);
  }

  public boolean isLeftClick() {
    return equals(LEFT_CLICK) || equals(SHIFT_LEFT_CLICK);
  }

  public boolean isShiftClick() {
    return equals(SHIFT_LEFT_CLICK) || equals(SHIFT_RIGHT_CLICK);
  }

}
