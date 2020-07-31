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
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public interface DataAdapter<K> {

    void store(@NonNull final PersistenceStorage persistenceStorage, final K value,
               @NonNull final NBTCompound nbtCompound);

    K parse(@NonNull final PersistenceStorage persistenceStorage, @NonNull Object parentObject,
            @NonNull final NBTCompound nbtCompound);
}
