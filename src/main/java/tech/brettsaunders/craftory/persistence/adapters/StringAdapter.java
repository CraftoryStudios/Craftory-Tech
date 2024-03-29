/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.NoArgsConstructor;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class StringAdapter implements DataAdapter<String> {

  @Override
  public void store(PersistenceStorage persistenceStorage, String value, NBTCompound nbtCompound) {
    nbtCompound.setString("data", value);
  }

  @Override
  public String parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey("data"))) {
      return null;
    }
    return nbtCompound.getString("data");
  }
}
