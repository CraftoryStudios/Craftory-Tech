/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.UUID;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class UUIDAdapter implements DataAdapter<UUID> {

  @Override
  public void store(PersistenceStorage persistenceStorage, UUID value,
      NBTCompound nbtCompound) {
    if (value == null) {
      return;
    }
    nbtCompound.setString("uuid", value.toString());
  }

  @Override
  public UUID parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    String uuid = nbtCompound.getString("uuid");
    if (uuid == null) {
      return null;
    }
    return UUID.fromString(uuid);
  }
}
