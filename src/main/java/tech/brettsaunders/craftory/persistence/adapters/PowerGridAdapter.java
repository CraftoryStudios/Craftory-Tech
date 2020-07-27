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
import java.util.HashSet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGrid;

@NoArgsConstructor
public class PowerGridAdapter implements DataAdapter<PowerGrid> {

    @Override
    public void store(@NonNull final PersistenceStorage persistenceStorage, @NonNull final PowerGrid value, @NonNull final NBTCompound nbtCompound) {
        NBTCompound cellsCompound = nbtCompound.addCompound("cells");
        persistenceStorage.saveObject(value.getCells(), cellsCompound);
        NBTCompound machinesCompound = nbtCompound.addCompound("machines");
        persistenceStorage.saveObject(value.getMachines(), machinesCompound);
        NBTCompound generatorsCompound = nbtCompound.addCompound("generators");
        persistenceStorage.saveObject(value.getGenerators(), generatorsCompound);
    }

    @Override
    public PowerGrid parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        NBTCompound cellsCompound = nbtCompound.addCompound("cells");
        NBTCompound machinesCompound = nbtCompound.addCompound("machines");
        NBTCompound generatorsCompound = nbtCompound.addCompound("generators");

        PowerGrid powerGrid = new PowerGrid();
        powerGrid.setCells(persistenceStorage.loadObject(parentObject, HashSet.class, cellsCompound));
        powerGrid.setMachines(persistenceStorage.loadObject(parentObject, HashSet.class, machinesCompound));
        powerGrid.setGenerators(persistenceStorage.loadObject(parentObject, HashSet.class, generatorsCompound));

        return powerGrid;
    }
}
