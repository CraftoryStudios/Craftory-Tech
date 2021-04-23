/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.Utilities.convertWorldChunkIDToChunkID;
import static tech.brettsaunders.craftory.Utilities.getChunkWorldID;
import static tech.brettsaunders.craftory.Utilities.getLocationID;
import static tech.brettsaunders.craftory.Utilities.getRegionID;
import static tech.brettsaunders.craftory.Utilities.keyToLoc;
import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.World;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Log;

public class CustomBlockStorage {

  private CustomBlockStorage() {
    throw new IllegalStateException("Utils Class");
  }

  /* Saving */
  @Synchronized
  public static void saveAllCustomChunks(String dataFolder, PersistenceStorage persistenceStorage,
      Map<String, HashSet<CustomBlock>> active,
      Map<String, HashSet<CustomBlock>> inactive, boolean autoSave) {
    Log.info("Saving Custom Block Data");
    active.forEach((chunk, customBlocks) -> saveCustomChunk(convertWorldChunkIDToChunkID(chunk), customBlocks, dataFolder,
        persistenceStorage, autoSave));
    inactive.forEach(((chunk, customBlocks) -> saveCustomChunk(convertWorldChunkIDToChunkID(chunk), customBlocks, dataFolder,
        persistenceStorage, autoSave)));
    Log.info("Saved Custom Block Data");
  }

  @Synchronized
  public static void saveCustomChunk(String chunkID, Set<CustomBlock> customBlocks,
      String dataFolder, PersistenceStorage persistenceStorage, boolean autoSave) {

      customBlocks.stream().findFirst().ifPresent(customBlock -> {
        Location firstBlock = customBlock.location;
        try {
          NBTFile nbtFile = new NBTFile(
              new File(dataFolder + File.separator + firstBlock.getWorld().getName(),
                  getRegionID(firstBlock.getChunk())));
          NBTCompound chunkCompound = nbtFile.addCompound(chunkID);
          customBlocks.forEach(customBlockValue -> {
            if (!autoSave) {
              customBlockValue.beforeSaveUpdate();
            }
            NBTCompound locationNBTSection = chunkCompound
                .addCompound(getLocationID(customBlockValue.location));
            persistenceStorage.saveFields(customBlockValue, locationNBTSection);
          });

          nbtFile.save();
        } catch (IOException e) {
          e.printStackTrace();
          sentryLog(e);
        }
      });

  }

  /**
   * Loading
   */
  @Synchronized
  public static void loadAllSavedRegions(World world, String dataFolder, CustomBlockManager manager,
      PersistenceStorage persistenceStorage) {
    int regions = 0;
    File directory = new File(dataFolder + File.separator + world.getName());
    if (directory.exists()) {
      File[] filesList = directory.listFiles();
      if (filesList == null) return;
      for (File file : filesList) {
        loadSavedRegion(world, file.getName(), dataFolder, manager, persistenceStorage);
        regions++;
      }
    }
    Log.info("Loaded " + regions + " region data files for world " + world.getName() + "!");
  }

  @Synchronized
  public static void loadSavedRegion(World world, String regionID, String dataFolder,
      CustomBlockManager manager, PersistenceStorage persistenceStorage) {
    try {
      NBTCompound chunkCompound;
      NBTCompound locationCompound = null;
      Location location;
      CustomBlock customBlock;

      NBTFile nbtFile = new NBTFile(
          new File(dataFolder + File.separator + world.getName(), regionID));
      for (String chunkKey : nbtFile.getKeys()) {

        chunkCompound = nbtFile.getCompound(chunkKey);
        if (chunkCompound.getKeys().isEmpty()) {
          continue;
        }

        HashSet<String> toDeleteFuelItem = new HashSet<>();

        HashSet<CustomBlock> chunkData = new HashSet<>();

        String chunkWorldKey = "";
        for (String locationKey : chunkCompound.getKeys()) {
          try {
            locationCompound = chunkCompound.getCompound(locationKey);
            location = keyToLoc(locationKey, world);
            customBlock = Craftory.customBlockFactory
                .createLoad(locationCompound, persistenceStorage, location);
            if (chunkWorldKey.isEmpty()) {
              chunkWorldKey = getChunkWorldID(customBlock.location.getChunk());
            }
            chunkData.add(customBlock);
          } catch (Exception e) {
            sentryLog(e);
            Log.warn(e.getMessage());
            Log.debug(Arrays.toString(e.getStackTrace()));
            Log.debug("Location Key: " + locationKey);
            Log.debug(
                locationCompound != null ? locationCompound.toString() : "NO Location Compound");
          }
        }

        for (String key : toDeleteFuelItem) {
          chunkCompound.getCompound(key).removeKey("fuelItem");
        }

        manager.getInactiveChunks().put(chunkWorldKey, chunkData);
      }
      nbtFile.save();
    } catch (IOException e) {
      sentryLog(e);
      e.printStackTrace();
    }
  }

}
