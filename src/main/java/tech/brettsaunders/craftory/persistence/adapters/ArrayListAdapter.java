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
import java.util.ArrayList;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Logger;

@NoArgsConstructor
public class ArrayListAdapter implements DataAdapter<ArrayList<?>> {

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final ArrayList<?> value, @NonNull final NBTCompound nbtCompound) {
    if (value.size() == 0 || value.get(0) == null) {
      return;
    }
    nbtCompound.setString("dataclass", value.get(0).getClass().getName());
    for (int i = 0; i < value.size(); i++) {
      NBTCompound container = nbtCompound.addCompound("" + i);
      persistenceStorage.saveObject(value.get(i), container);
    }
  }

  @Override
  public ArrayList<Object> parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    Class dataClass;
    ArrayList<Object> arrayList = new ArrayList<>();
    if (nbtCompound.getKeys().size() == 0) {
      return arrayList;
    }
    try {
      dataClass = Class.forName(nbtCompound.getString("dataclass"));
      for (String key : nbtCompound.getKeys()) {
        if (key.equals("dataclass")) {
          continue;
        }
        NBTCompound container = nbtCompound.getCompound(key);
        arrayList.add(persistenceStorage.loadObject(parentObject, dataClass, container));
      }
    } catch (ClassNotFoundException ex) {
      Logger.error(nbtCompound.getString("dataclass"));
      ex.printStackTrace();
    }
    return arrayList;
  }
}
