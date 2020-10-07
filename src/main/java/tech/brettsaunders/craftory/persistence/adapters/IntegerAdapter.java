/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class IntegerAdapter implements DataAdapter<Integer> {

  public static final String INT = "int";

  @Override
  public void store(PersistenceStorage persistenceStorage, Integer value, NBTCompound nbtCompound) {
    nbtCompound.setInteger(INT, value);
  }

  @Override
  public Integer parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey(INT))) {
      return null;
    }
    return nbtCompound.getInteger(INT);
  }
}
