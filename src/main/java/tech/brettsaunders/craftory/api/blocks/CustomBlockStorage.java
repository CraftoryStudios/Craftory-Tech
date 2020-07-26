/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Utilities.convertWorldChunkIDToChunkID;
import static tech.brettsaunders.craftory.Utilities.getChunkWorldID;
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
import java.util.Optional;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.core.block.generators.SolidFuelGenerator;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomBlockStorage {

  /* Saving */
  @Synchronized
  public static void saveAllCustomChunks(String dataFolder, PersistenceStorage persistenceStorage,HashMap<String, HashSet<CustomBlock>> active, HashMap<String, HashSet<CustomBlock>> inactive) {
    Logger.info("Saving Custom Block Data");
    active.forEach((chunk, customBlocks) -> {
      saveCustomChunk(convertWorldChunkIDToChunkID(chunk), customBlocks, dataFolder, persistenceStorage);
    });
    inactive.forEach(((chunk, customBlocks) -> {
      saveCustomChunk(convertWorldChunkIDToChunkID(chunk), customBlocks, dataFolder, persistenceStorage);
    }));
    Logger.info("Saved Custom Block Data");
  }

  @Synchronized
  public static void saveCustomChunk(String chunkID, HashSet<CustomBlock> customBlocks, String dataFolder, PersistenceStorage persistenceStorage) {
    try {
      Optional<CustomBlock> customBlockFirst = Optional.of(customBlocks.stream().findFirst().get());
      if (!customBlockFirst.isPresent()) {
        return;
      }
      Location firstBlock = customBlockFirst.get().location;
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
  public static void loadAllSavedRegions(World world, String dataFolder, CustomBlockManager manager, PersistenceStorage persistenceStorage) {
    int regions = 0;
      File directory = new File(dataFolder + File.separator + world.getName());
      if (directory.exists()) {
        File[] filesList = directory.listFiles();
        for (File file : filesList) {
          loadSavedRegion(world, file.getName(), dataFolder, manager, persistenceStorage);
          regions++;
        }
      }
    Logger.info("Loaded " + regions + " region data files for world "+world.getName() + "!");
  }

  @Synchronized
  public static void loadSavedRegion(World world, String regionID, String dataFolder, CustomBlockManager manager, PersistenceStorage persistenceStorage) {
    try {
      NBTCompound chunkCompound;
      NBTCompound locationCompound = null;
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

        String chunkWorldKey = "";
        for (String locationKey : chunkCompound.getKeys()) {
          try {
            locationCompound = chunkCompound.getCompound(locationKey);
            //TODO Remove exception later version
            if (locationCompound.getCompound("blockName").getString("data")
                .equalsIgnoreCase("CopperOre")) {
              toDelete.add(locationKey);
              continue;
            }
            location = keyToLoc(locationKey, world);
            customBlock = Craftory.customBlockFactory
                .createLoad(locationCompound, persistenceStorage, location);
            if (chunkWorldKey.isEmpty()) {
              chunkWorldKey = getChunkWorldID(customBlock.location.getChunk());
            }
            //TODO Remove in later version
            if (locationCompound.hasKey("fuelItem") && customBlock instanceof SolidFuelGenerator) {
              ItemStack fuelItem = NBTItem
                  .convertNBTtoItem(locationCompound.getCompound("fuelItem"));
              ((SolidFuelGenerator) customBlock).setFuelItem(fuelItem);
              toDeleteFuelItem.add(locationKey);
            }
            chunkData.add(customBlock);
          } catch (Exception e) {
            Logger.info(e.getMessage());
            Logger.info(e.getStackTrace().toString());
            Logger.debug("Location Key: " + locationKey);
            Logger.debug(locationCompound != null ? locationCompound.toString() : "NO Location Compound");
          }
        }
        //TODO Remove Later
        for (String key : toDelete) {
          chunkCompound.removeKey(key);
        }

        for (String key : toDeleteFuelItem) {
          chunkCompound.getCompound(key).removeKey("fuelItem");
        }

        manager.getInactiveChunks().put(chunkWorldKey, chunkData);
      }
      nbtFile.save();
    } catch (IOException e) {
        e.printStackTrace();
      }
  }

}
