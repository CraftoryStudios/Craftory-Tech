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
        persistenceStorage.getConverterClass(value.get(0)).getName());
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
    if (nbtCompound.getKeys().isEmpty()) {
      return arrayList;
    }
    try {
      if (nbtCompound.getString(DATACLASS).endsWith("ItemStack")) {
        dataClass = ItemStack.class;
      } else {
        dataClass = Class.forName(nbtCompound.getString(DATACLASS));
      }
      for (String key : nbtCompound.getKeys()) {
        if (key.equals(DATACLASS)) {
          continue;
        }
        NBTCompound container = nbtCompound.getCompound(key);
        arrayList.add(persistenceStorage.loadObject(parentObject, dataClass, container));
      }
    } catch (ClassNotFoundException ex) {
      Log.error(nbtCompound.getString(DATACLASS));
      ex.printStackTrace();
    }
    return arrayList;
  }
}
