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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class HashMapAdapter implements DataAdapter<HashMap<?, ?>> {

    @Override
    public void store(@NonNull final PersistenceStorage persistenceStorage, @NonNull final HashMap<?, ?> value, @NonNull final NBTCompound nbtCompound) {
        nbtCompound.getKeys().forEach(key -> {if (nbtCompound.hasKey(key)) nbtCompound.removeKey(key);});
        value.forEach((entryKey, entryValue) -> {
            NBTCompound container = nbtCompound.addCompound("" + entryKey.hashCode());
            NBTCompound keyData = container.addCompound("key");
            NBTCompound data = container.addCompound("data");
            container.setString("keyclass", persistenceStorage.saveObject(entryKey, keyData).getName());
            container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getName());
        });
    }

    @Override
    public HashMap<Object, Object> parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        HashMap<Object, Object> map = new HashMap<>();
        for (String key : nbtCompound.getKeys()) {
            NBTCompound container = nbtCompound.getCompound(key);
            NBTCompound keyData = container.getCompound("key");
            NBTCompound data = container.getCompound("data");
            try {
                map.put(persistenceStorage.loadObject(parentObject, Class.forName(container.getString("keyclass")), keyData),
                        persistenceStorage.loadObject(parentObject, Class.forName(container.getString("dataclass")), data));
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }
}
