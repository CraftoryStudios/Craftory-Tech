package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.ArrayList;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

@NoArgsConstructor
public class ArrayListAdapter implements DataAdapter<ArrayList<?>> {

    @Override
    public void store(@NonNull final PersistenceStorage persistenceStorage, @NonNull final ArrayList<?> value, @NonNull final NBTCompound nbtCompound) {
        value.forEach(entryValue -> {
            NBTCompound container = nbtCompound.addCompound("" + entryValue.hashCode());
            NBTCompound data = container.addCompound("data");
            container.setString("dataclass", persistenceStorage.saveObject(entryValue, data).getName());
        });
    }

    @Override
    public ArrayList<Object> parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        ArrayList<Object> map = new ArrayList<>();
        for (String key : nbtCompound.getKeys()) {
            NBTCompound container = nbtCompound.getCompound(key);
            NBTCompound data = container.addCompound("data");
            try {
                map.add(persistenceStorage.loadObject(parentObject, Class.forName(container.getString("dataclass")), data));
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }
}
