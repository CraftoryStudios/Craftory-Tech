package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Utilities.getLocationID;
import static tech.brettsaunders.craftory.Utilities.getRegionID;
import static tech.brettsaunders.craftory.Utilities.keyToLoc;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelGenerator;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomBlockStorage {

  /* Saving */
  @Synchronized
  public static void saveAllCustomChunks(String dataFolder, PersistenceStorage persistenceStorage,HashMap<String, HashSet<CustomBlock>> active, HashMap<String, HashSet<CustomBlock>> inactive) {
    Logger.info("Saving Custom Block Data");
    active.forEach((chunk, customBlocks) -> {
      saveCustomChunk(chunk, customBlocks, dataFolder, persistenceStorage);
    });
    inactive.forEach(((chunk, customBlocks) -> {
      saveCustomChunk(chunk, customBlocks, dataFolder, persistenceStorage);
    }));
    Logger.info("Saved Custom Block Data");
  }

  @Synchronized
  public static void saveCustomChunk(String chunkID, HashSet<CustomBlock> customBlocks, String dataFolder, PersistenceStorage persistenceStorage) {
    try {
      Location firstBlock = customBlocks.stream().findFirst().get().location;
      if (firstBlock == null) {
        return;
      }
      NBTFile nbtFile = new NBTFile(
          new File(dataFolder + File.separator + firstBlock.getWorld().getName(),
              getRegionID(firstBlock.getChunk())));
      NBTCompound chunkCompound = nbtFile.addCompound(chunkID);
      customBlocks.forEach(customBlock -> {
        customBlock.beforeSaveUpdate();
        NBTCompound locationNBTSection = chunkCompound
            .addCompound(getLocationID(customBlock.location));
        persistenceStorage.saveFields(customBlock, locationNBTSection);
      });
      nbtFile.save();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Loading */
  @Synchronized
  public static void loadAllSavedRegions(String dataFolder, CustomBlockManager manager, PersistenceStorage persistenceStorage) {
    int regions = 0;
    for (World world : Bukkit.getWorlds()) {
      File directory = new File(dataFolder + File.separator + world.getName());
      if (directory.exists()) {
        File[] filesList = directory.listFiles();
        for (File file : filesList) {
          loadSavedRegion(world, file.getName(), dataFolder, manager, persistenceStorage);
          regions++;
        }
      }
    }
    Logger.info("Loaded " + regions + " region data files!");
  }

  @Synchronized
  public static void loadSavedRegion(World world, String regionID, String dataFolder, CustomBlockManager manager, PersistenceStorage persistenceStorage) {
    try {
      NBTCompound chunkCompound;
      NBTCompound locationCompound;
      Location location;
      CustomBlock customBlock;

      NBTFile nbtFile = new NBTFile(
          new File(dataFolder + File.separator + world.getName(), regionID));
      if (nbtFile == null) {
        return;
      }
      for (String chunkKey : nbtFile.getKeys()) {

        chunkCompound = nbtFile.getCompound(chunkKey);
        if (chunkCompound == null || chunkCompound.getKeys().size() == 0) {
          continue;
        }

        //TODO Remove later
        HashSet<String> toDelete = new HashSet<>();
        HashSet<String> toDeleteFuelItem = new HashSet<>();

        HashSet<CustomBlock> chunkData = new HashSet<>();

        for (String locationKey : chunkCompound.getKeys()) {
          locationCompound = chunkCompound.getCompound(locationKey);
          //TODO Remove exception later version
          if (locationCompound.getCompound("blockName").getString("data").equalsIgnoreCase("CopperOre")) {
            toDelete.add(locationKey);
            continue;
          }
          location = keyToLoc(locationKey, world);
          customBlock = Craftory.customBlockFactory.createLoad(locationCompound, persistenceStorage, location);
          //TODO Remove in later version
          if (locationCompound.hasKey("fuelItem") && customBlock instanceof SolidFuelGenerator) {
            ItemStack fuelItem = NBTItem.convertNBTtoItem(locationCompound.getCompound("fuelItem"));
            ((SolidFuelGenerator) customBlock).setFuelItem(fuelItem);
            toDeleteFuelItem.add(locationKey);
          }
          chunkData.add(customBlock);
        }
        //TODO Remove Later
        for (String key : toDelete) {
          chunkCompound.removeKey(key);
        }

        for (String key : toDeleteFuelItem) {
          chunkCompound.getCompound(key).removeKey("fuelItem");
        }

        manager.getInactiveChunks().put(chunkKey, chunkData);
      }
      nbtFile.save();
    } catch (IOException e) {
        e.printStackTrace();
      }
  }

}
