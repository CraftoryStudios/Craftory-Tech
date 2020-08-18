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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class HashMapAdapter implements DataAdapter<Object2ObjectOpenHashMap<?, ?>> {

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final Object2ObjectOpenHashMap<?, ?> value, @NonNull final NBTCompound nbtCompound) {
    NBTCompound parent = nbtCompound.getParent();
    String name = nbtCompound.getName();
    nbtCompound.getParent().removeKey(name);
    parent.addCompound(name);

    value.forEach((entryKey, entryValue) -> {
      NBTCompound container = nbtCompound.addCompound("" + entryKey.hashCode());
      NBTCompound keyData = container.addCompound("key");
      NBTCompound data = container.addCompound("data");
      container.setString("keyclass", persistenceStorage.saveObject(entryKey, keyData).getName());
      container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getName());
    });
  }

  @Override
  public Object2ObjectOpenHashMap<Object, Object> parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    Object2ObjectOpenHashMap<Object, Object> map = new Object2ObjectOpenHashMap<>();
    for (String key : nbtCompound.getKeys()) {
      NBTCompound container = nbtCompound.getCompound(key);
      NBTCompound keyData = container.getCompound("key");
      NBTCompound data = container.getCompound("data");
      try {
        map.put(persistenceStorage
                .loadObject(parentObject, Class.forName(container.getString("keyclass")), keyData),
            persistenceStorage
                .loadObject(parentObject, Class.forName(container.getString("dataclass")), data));
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      }
    }
    return map;
  }
}
