package tech.brettsaunders.craftory.persistence.adapters;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class LocationAdapter implements DataAdapter<Location> {

    @Override
    public void store(PersistenceStorage persistenceStorage, Location value, NBTCompound nbtCompound) {
        if (value != null) {
            return;
        }
        nbtCompound.setString("world", value.getWorld().getName());
        nbtCompound.setInteger("x", value.getBlockX());
        nbtCompound.setInteger("y", value.getBlockY());
        nbtCompound.setInteger("z", value.getBlockZ());
    }

    @Override
    public Location parse(PersistenceStorage persistenceStorage, Object parentObject,
                                   NBTCompound nbtCompound) {
        World world = Bukkit.getWorld(nbtCompound.getString("world"));
        if (world == null) {
            return null;
        }
        return new Location(world, nbtCompound.getInteger("x"), nbtCompound.getInteger("y"),
                nbtCompound.getInteger("z"));
    }
}
