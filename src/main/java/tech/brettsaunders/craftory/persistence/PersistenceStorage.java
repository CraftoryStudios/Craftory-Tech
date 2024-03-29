/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence;

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import com.google.gson.Gson;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.persistence.adapters.ArrayListAdapter;
import tech.brettsaunders.craftory.persistence.adapters.BlockFaceAdapter;
import tech.brettsaunders.craftory.persistence.adapters.BooleanAdapter;
import tech.brettsaunders.craftory.persistence.adapters.DataAdapter;
import tech.brettsaunders.craftory.persistence.adapters.EnergyStorageAdapter;
import tech.brettsaunders.craftory.persistence.adapters.FluidStorageAdapter;
import tech.brettsaunders.craftory.persistence.adapters.HashMapAdapter;
import tech.brettsaunders.craftory.persistence.adapters.HashSetAdapter;
import tech.brettsaunders.craftory.persistence.adapters.IntegerAdapter;
import tech.brettsaunders.craftory.persistence.adapters.InteractableBlockAdapter;
import tech.brettsaunders.craftory.persistence.adapters.ItemStackAdapter;
import tech.brettsaunders.craftory.persistence.adapters.LocationAdapter;
import tech.brettsaunders.craftory.persistence.adapters.LongAdapter;
import tech.brettsaunders.craftory.persistence.adapters.PowerGridAdapter;
import tech.brettsaunders.craftory.persistence.adapters.StringAdapter;
import tech.brettsaunders.craftory.persistence.adapters.UUIDAdapter;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.fluids.FluidStorage;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerGrid;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.ReflectionUtils;

public class PersistenceStorage {

  private final Gson gson;
  private final HashMap<Class<?>, DataAdapter<?>> converters;
  private final HashMap<Class<?>, DataAdapter<?>> interfaceConverters;
  @Getter
  private final PersistenceTable persistenceTable;

  public PersistenceStorage() {
    gson = new Gson();
    converters = new HashMap<>();
    persistenceTable = new PersistenceTable();

    // Register default converters
    registerDataConverter(String.class, new StringAdapter());
    registerDataConverter(int.class, new IntegerAdapter());
    registerDataConverter(Integer.class, new IntegerAdapter());
    registerDataConverter(Long.class, new LongAdapter());
    registerDataConverter(HashMap.class, new HashMapAdapter());
    registerDataConverter(Location.class, new LocationAdapter());
    registerDataConverter(HashSet.class, new HashSetAdapter());
    registerDataConverter(EnergyStorage.class, new EnergyStorageAdapter());
    registerDataConverter(BlockFace.class, new BlockFaceAdapter());
    registerDataConverter(INTERACTABLEBLOCK.class, new InteractableBlockAdapter());
    registerDataConverter(ArrayList.class, new ArrayListAdapter());
    registerDataConverter(Boolean.class, new BooleanAdapter());
    registerDataConverter(PowerGrid.class, new PowerGridAdapter());
    registerDataConverter(FluidStorage.class, new FluidStorageAdapter());
    registerDataConverter(UUID.class, new UUIDAdapter());

    // Register interface converters
    interfaceConverters = new HashMap<>();
    registerInterfaceConverter(ItemStack.class, new ItemStackAdapter());
  }

  public <T> void registerInterfaceConverter(@NonNull Class<T> clazz,
      @NonNull DataAdapter<? extends T> converter) {
      interfaceConverters.putIfAbsent(clazz, converter);
  }

  public <T> void registerDataConverter(@NonNull Class<T> clazz,
      @NonNull DataAdapter<? extends T> converter) {
      converters.putIfAbsent(clazz, converter);
  }

  public void saveFields(Object object, NBTCompound nbtCompound) {
    if (object == null || nbtCompound == null) {
      Log.error("Couldn't save object! Null");
      return;
    }
    ReflectionUtils.getFieldsRecursively(object.getClass(), Object.class).stream()
        .filter(field -> field.getAnnotation(Persistent.class) != null).forEach(field -> {
      field.setAccessible(true);
      try {
        if (field.get(object) != null) {
          saveObject(field.get(object), nbtCompound.addCompound(field.getName()));
        }
      } catch (IllegalAccessException e) {
        sentryLog(e);
        throw new IllegalStateException(
            "Unable to save field " + object.getClass().getSimpleName() + "." + field.getName(), e);
      }
    });
  }

  public void loadFields(@NonNull Object object, @NonNull NBTCompound nbtCompound) {
    loadFieldsDataObject(object, object, nbtCompound);
  }

  public void loadFieldsDataObject(@NonNull Object parent, @NonNull Object object,
      @NonNull NBTCompound nbtCompound) {
    ReflectionUtils.getFieldsRecursively(object.getClass(), Object.class).stream()
        .filter(field -> field.getAnnotation(Persistent.class) != null).forEach(field -> {
      field.setAccessible(true);
      if (Boolean.TRUE.equals(nbtCompound.hasKey(field.getName()))) {
        try {
          Object obj = loadObject(parent, field.getType(),
              nbtCompound.getCompound(field.getName()));
          if (obj != null || !(field.getType() == int.class || field.getType() == float.class
              || field.getType() == double.class)) {
            field.set(object, obj);
          }
        } catch (IllegalAccessException e) {
          sentryLog(e);
          throw new IllegalStateException(
              "Unable to load field " + object.getClass().getSimpleName() + "." + field.getName(),
              e);
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  public Class<?> saveObject(Object data, NBTCompound nbtCompound) {
    if (data == null || nbtCompound == null) {
      Log.error("Error Saving Object. Null");
      return null;
    }
    Class<?> clazz = data.getClass();

    // Check custom converters
    if (converters.containsKey(data.getClass())) {
      ((DataAdapter<Object>) converters.get(clazz)).store(this, data, nbtCompound);
      return clazz;
    }

    for (Entry<Class<?>, DataAdapter<?>> entry : interfaceConverters.entrySet()) {
      if (entry.getKey().isInstance(data)) {
        ((DataAdapter<Object>) entry.getValue()).store(this, data, nbtCompound);
        return entry.getKey();
      }
    }

    // Fallback to Json
    Log.warn(
        "Did not find a Wrapper for " + data.getClass().getName() + "! Falling back to Gson!");
    nbtCompound.setString("json", gson.toJson(data));
    return null;
  }

  public Class<?> getConverterClass(Object data) {
    if (data == null) {
      Log.error("Error Saving Object. Null");
      return null;
    }

    Class<?> clazz = data.getClass();
    if (converters.containsKey(data.getClass())) {
      return clazz;
    }

    for (Entry<Class<?>, DataAdapter<?>> entry : interfaceConverters.entrySet()) {
      if (entry.getKey().isInstance(data)) {
        return entry.getKey();
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T loadObject(@NonNull Object parent, @NonNull Class<T> type,
      @NonNull NBTCompound nbtCompound) {
    if (converters.containsKey(type)) {
      return (T) converters.get(type).parse(this, parent, nbtCompound);
    }

    for (Entry<Class<?>, DataAdapter<?>> entry : interfaceConverters.entrySet()) {
      if (entry.getKey().isAssignableFrom(type)) {
        return (T) entry.getValue().parse(this, parent, nbtCompound);
      }
    }

    // Fallback to Json
    if (Boolean.TRUE.equals(nbtCompound.hasKey("json"))) {
      return gson.fromJson(nbtCompound.getString("json"), type);
    }

    return null;
  }
}
