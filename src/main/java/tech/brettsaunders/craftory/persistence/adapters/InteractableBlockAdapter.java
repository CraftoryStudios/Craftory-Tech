/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.Constants;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class InteractableBlockAdapter implements DataAdapter<INTERACTABLEBLOCK> {

  public static final String INTERACTABLEBLOCK = "INTERACTABLEBLOCK";

  @Override
  public void store(PersistenceStorage persistenceStorage, INTERACTABLEBLOCK value,
      NBTCompound nbtCompound) {
    nbtCompound.setString(INTERACTABLEBLOCK, value.name());
  }

  @Override
  public INTERACTABLEBLOCK parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey(INTERACTABLEBLOCK))) {
      return null;
    }
    return Constants.INTERACTABLEBLOCK.valueOf(nbtCompound.getString(INTERACTABLEBLOCK));
  }
}
