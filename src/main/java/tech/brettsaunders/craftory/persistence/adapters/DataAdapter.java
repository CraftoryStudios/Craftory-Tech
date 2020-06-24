package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public interface DataAdapter<K> {

    void store(@NonNull final PersistenceStorage persistenceStorage, final K value,
               @NonNull final NBTCompound nbtCompound);

    K parse(@NonNull final PersistenceStorage persistenceStorage, @NonNull Object parentObject,
            @NonNull final NBTCompound nbtCompound);
}
