/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.ArrayList;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Log;

@NoArgsConstructor
public class ArrayListAdapter implements DataAdapter<ArrayList<?>> {

  public static final String DATACLASS = "dataclass";

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final ArrayList<?> value, @NonNull final NBTCompound nbtCompound) {
    if (value.isEmpty() || value.get(0) == null) {
      return;
    }
    nbtCompound.setString(DATACLASS,
        persistenceStorage.getConverterClass(value.get(0)).getSimpleName());
    for (int i = 0; i < value.size(); i++) {
      NBTCompound container = nbtCompound.addCompound("" + i);
      persistenceStorage.saveObject(value.get(i), container);
    }
  }

  @Override
  public ArrayList<Object> parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    Optional<Class> dataClass;
    ArrayList<Object> arrayList = new ArrayList<>();
    if (nbtCompound.getKeys().isEmpty()) {
      return arrayList;
    }
    if (nbtCompound.getString(DATACLASS).endsWith("ItemStack")) {
      dataClass = Optional.of(ItemStack.class);
    } else {
      dataClass = Optional.ofNullable(persistenceStorage.getPersistenceTable().referenceTable.get(nbtCompound.getString(DATACLASS)));
    }

    if (dataClass.isPresent()) {
      for (String key : nbtCompound.getKeys()) {
        if (key.equals(DATACLASS)) {
          continue;
        }
        NBTCompound container = nbtCompound.getCompound(key);
        arrayList.add(persistenceStorage.loadObject(parentObject, dataClass.get(), container));
      }
    } else {
      Log.warn("Failure to load ArrayList");
    }
    return arrayList;
  }
}
