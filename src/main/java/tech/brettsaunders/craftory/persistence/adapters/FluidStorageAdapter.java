/*******************************************************************************
 * Copyright (c) 2020. MatthewJones & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Matthew Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.fluids.FluidStorage;

public class FluidStorageAdapter implements DataAdapter<FluidStorage> {

    @Override
    public void store(PersistenceStorage persistenceStorage, FluidStorage value, NBTCompound nbtCompound) {
        if (value == null) {
            return;
        }
        nbtCompound.setInteger("fluid", value.getFluidStored());
        nbtCompound.setInteger("capacity", value.getMaxFluidStored());
        nbtCompound.setInteger("maxReceive", value.getMaxReceive());
        nbtCompound.setInteger("maxExtract", value.getMaxExtract());
    }

    @Override
    public FluidStorage parse(PersistenceStorage persistenceStorage, Object parentObject,
                                   NBTCompound nbtCompound) {
        return new FluidStorage(nbtCompound.getInteger("fluid"), nbtCompound.getInteger("capacity"), nbtCompound.getInteger("maxReceive"), nbtCompound.getInteger("maxExtract"));
    }
}
