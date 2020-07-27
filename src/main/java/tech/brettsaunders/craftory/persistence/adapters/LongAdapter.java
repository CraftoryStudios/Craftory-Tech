/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class LongAdapter implements DataAdapter<Long> {

    @Override
    public void store(PersistenceStorage persistenceStorage, Long value, NBTCompound nbtCompound) {
        nbtCompound.setLong("long", value);
    }

    @Override
    public Long parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        if (!nbtCompound.hasKey("long")) {
            return null;
        }
        return nbtCompound.getLong("long");
    }
}
