/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

public class VariableContainer<T> {

  private T t;

  public VariableContainer(T t) {
    this.t = t;
  }

  public T getT() {
    return t;
  }

  public void setT(T t) {
    this.t = t;
  }
}
