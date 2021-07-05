/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.HashMap;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerGrid;

@NoArgsConstructor
public class PowerGridAdapter implements DataAdapter<PowerGrid> {

  @Override
  public void store(@NonNull final PersistenceStorage persistenceStorage,
      @NonNull final PowerGrid value, @NonNull final NBTCompound nbtCompound) {
    NBTCompound blockConnectionsCompound = nbtCompound.addCompound("blockConnections");
    persistenceStorage.saveObject(value.getBlockConnections(), blockConnectionsCompound);
    NBTCompound powerConnectorsCompound = nbtCompound.addCompound("powerConnectors");
    persistenceStorage.saveObject(value.getPowerConnectors(), powerConnectorsCompound);
  }

  @Override
  public PowerGrid parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    NBTCompound blockConnectionsCompound = nbtCompound.addCompound("blockConnections");
    NBTCompound powerConnectorsCompound = nbtCompound.addCompound("powerConnectors");

    PowerGrid powerGrid = new PowerGrid();
    powerGrid.setBlockConnections(
        persistenceStorage.loadObject(parentObject, HashMap.class, blockConnectionsCompound));
    powerGrid.setPowerConnectors(
        persistenceStorage.loadObject(parentObject, HashMap.class, powerConnectorsCompound));
    powerGrid.findPoweredBlocks();
    return powerGrid;
  }
}
