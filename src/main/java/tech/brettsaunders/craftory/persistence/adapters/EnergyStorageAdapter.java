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
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;

public class EnergyStorageAdapter implements DataAdapter<EnergyStorage> {

    @Override
    public void store(PersistenceStorage persistenceStorage, EnergyStorage value, NBTCompound nbtCompound) {
        if (value == null) {
            return;
        }
        nbtCompound.setInteger("energy", value.getEnergyStored());
        nbtCompound.setInteger("capacity", value.getMaxEnergyStored());
        nbtCompound.setInteger("maxReceive", value.getMaxReceive());
        nbtCompound.setInteger("maxExtract", value.getMaxExtract());
    }

    @Override
    public EnergyStorage parse(PersistenceStorage persistenceStorage, Object parentObject,
                                   NBTCompound nbtCompound) {
        return new EnergyStorage(nbtCompound.getInteger("energy"), nbtCompound.getInteger("capacity"), nbtCompound.getInteger("maxReceive"), nbtCompound.getInteger("maxExtract"));
    }
}
