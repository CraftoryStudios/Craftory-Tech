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
import java.util.HashMap;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Log;

@NoArgsConstructor
public class HashMapAdapter implements DataAdapter<HashMap<?, ?>> {

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final HashMap<?, ?> value, @NonNull final NBTCompound nbtCompound) {
    NBTCompound parent = nbtCompound.getParent();
    String name = nbtCompound.getName();
    nbtCompound.getParent().removeKey(name);
    parent.addCompound(name);

    value.forEach((entryKey, entryValue) -> {
      NBTCompound container = nbtCompound.addCompound("" + entryKey.hashCode());
      NBTCompound keyData = container.addCompound("key");
      NBTCompound data = container.addCompound("data");
      container.setString("keyclass", persistenceStorage.saveObject(entryKey, keyData).getSimpleName());
      container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getSimpleName());
    });
  }

  @Override
  public HashMap<Object, Object> parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    HashMap<Object, Object> map = new HashMap<>();
    for (String key : nbtCompound.getKeys()) {
      NBTCompound container = nbtCompound.getCompound(key);
      NBTCompound keyData = container.getCompound("key");
      NBTCompound data = container.getCompound("data");

      Optional<Class> keyClass = Optional.ofNullable(persistenceStorage.getPersistenceTable().referenceTable.get(container.getString("keyclass")));
      Optional<Class> dataClass = Optional.ofNullable(persistenceStorage.getPersistenceTable().referenceTable.get(container.getString("dataclass")));

      if (keyClass.isPresent() && dataClass.isPresent()) {
      map.put(persistenceStorage
                .loadObject(parentObject,keyClass.get(), keyData),
            persistenceStorage
                .loadObject(parentObject, dataClass.get(), data));
      } else {
        Log.warn("HashMap fail");
      }
    }
    return map;
  }
}
