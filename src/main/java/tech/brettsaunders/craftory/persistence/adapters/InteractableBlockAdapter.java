package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import tech.brettsaunders.craftory.CoreHolder.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class InteractableBlockAdapter implements DataAdapter<INTERACTABLEBLOCK> {

    @Override
    public void store(PersistenceStorage persistenceStorage, INTERACTABLEBLOCK value, NBTCompound nbtCompound) {
        nbtCompound.setString("INTERACTABLEBLOCK", value.name());
    }

    @Override
    public INTERACTABLEBLOCK parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        if (!nbtCompound.hasKey("INTERACTABLEBLOCK")) {
            return null;
        }
        return INTERACTABLEBLOCK.valueOf(nbtCompound.getString("INTERACTABLEBLOCK"));
    }
}
