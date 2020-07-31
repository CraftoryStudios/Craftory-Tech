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
import java.util.HashSet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class HashSetAdapter implements DataAdapter<HashSet<?>> {

    @Override
    public void store(@NonNull final PersistenceStorage persistenceStorage, @NonNull final HashSet<?> value, @NonNull final NBTCompound nbtCompound) {
        value.forEach(entryValue -> {
            NBTCompound container = nbtCompound.addCompound("" + entryValue.hashCode());
            NBTCompound data = container.addCompound("data");
            container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getName());
        });
    }

    @Override
    public HashSet<Object> parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        HashSet<Object> set = new HashSet<>();
        if(nbtCompound.getKeys().size() == 0) return set;
        for (String key : nbtCompound.getKeys()) {
            NBTCompound container = nbtCompound.getCompound(key);
            NBTCompound data = container.getCompound("data");
            try {
                set.add(persistenceStorage.loadObject(parentObject, Class.forName(container.getString("dataclass")), data));
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return set;
    }
}
