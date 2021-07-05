/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class LongAdapter implements DataAdapter<Long> {

  @Override
  public void store(PersistenceStorage persistenceStorage, Long value, NBTCompound nbtCompound) {
    nbtCompound.setLong("long", value);
  }

  @Override
  public Long parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey("long"))) {
      return null;
    }
    return nbtCompound.getLong("long");
  }
}
