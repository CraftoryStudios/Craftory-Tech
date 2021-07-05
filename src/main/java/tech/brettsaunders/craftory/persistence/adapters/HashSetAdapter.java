/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.HashSet;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class HashSetAdapter implements DataAdapter<HashSet<?>> {

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final HashSet<?> value, @NonNull final NBTCompound nbtCompound) {
    value.forEach(entryValue -> {
      NBTCompound container = nbtCompound.addCompound("" + entryValue.hashCode());
      NBTCompound data = container.addCompound("data");
      container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getSimpleName());
    });
  }

  @Override
  public HashSet<Object> parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    HashSet<Object> set = new HashSet<>();
    if (nbtCompound.getKeys().isEmpty()) {
      return set;
    }


    for (String key : nbtCompound.getKeys()) {
      NBTCompound container = nbtCompound.getCompound(key);
      NBTCompound data = container.getCompound("data");
      Optional<Class> dataClass = Optional.ofNullable(persistenceStorage.getPersistenceTable().referenceTable.get(container.getString("dataclass")));

      if (dataClass.isPresent()) {
        set.add(persistenceStorage
            .loadObject(parentObject, dataClass.get(), data));
      }
    }
    return set;
  }
}
