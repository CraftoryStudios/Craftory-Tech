/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class BooleanAdapter implements DataAdapter<Boolean> {

  public static final String BOOLEAN = "boolean";

  @Override
  public void store(PersistenceStorage persistenceStorage, Boolean value, NBTCompound nbtCompound) {
    nbtCompound.setBoolean(BOOLEAN, value);
  }

  @Override
  public Boolean parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey(BOOLEAN))) {
      return false;
    }
    return nbtCompound.getBoolean(BOOLEAN);
  }
}
