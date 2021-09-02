/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Log;

public class CustomBlockFactory {

  private final HashMap<String, Class<? extends CustomBlock>> blockTypes = new HashMap<>();
  private final HashMap<String, Constructor<? extends CustomBlock>> createConstructor = new HashMap<>();
  private final HashMap<String, Constructor<? extends CustomBlock>> loadConstructor = new HashMap<>();
  private final HashSet<String> directional = new HashSet<>();
  private final String locationClassName;
  private final String playerClassName;

  public CustomBlockFactory() {
    locationClassName = Location.class.getName();
    playerClassName = Player.class.getName();
  }

  @Synchronized
  public void registerCustomBlock(String nameID, Class<? extends CustomBlock> block,
      boolean registerTickable, boolean directional) {
    blockTypes.put(nameID, block);
    Constructor[] constructors = block.getConstructors();
    for (Constructor<? extends CustomBlock> constructor : constructors) {
      if (constructor.getParameterCount() == 0) {
        loadConstructor.put(nameID, constructor);
      } else if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0]
          .getName().equals(locationClassName) && constructor.getParameterTypes()[1]
          .getName().equals(playerClassName)) {
        createConstructor.put(nameID, constructor);
      }
    }
    if (registerTickable) {
      Craftory.tickManager.registerCustomBlockClass(block);
    }
    if (directional) {
      this.directional.add(nameID);
    }
  }

  @Synchronized
  public String getKey(Class<? extends CustomBlock> block) {
    for (Entry<String, Class<? extends CustomBlock>> entry : blockTypes.entrySet()) {
      if (entry.getValue() == block) {
        return entry.getKey();
      }
    }
    return "";
  }

  @Synchronized
  public CustomBlock createLoad(NBTCompound locationCompound, PersistenceStorage persistenceStorage,
      Location location) {
    CustomBlock customBlock = null;
    NBTCompound nameCompound = locationCompound.getCompound("blockName");
    String nameID = nameCompound.getString("data");
    if (loadConstructor.containsKey(nameID)) {
      Constructor<? extends CustomBlock> constructor = loadConstructor.get(nameID);
      try {
        customBlock = constructor.newInstance();
        persistenceStorage.loadFields(customBlock, locationCompound);
        customBlock.setLocation(location);
        customBlock.afterLoadUpdate();
      } catch (Exception e) {
        e.printStackTrace();
        sentryLog(e);
      }
      return customBlock;
    }
    Log.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }

  @Synchronized
  public CustomBlock create(String nameID, Location location, BlockFace direction, Player player) {
    CustomBlock customBlock = null;
    if (createConstructor.containsKey(nameID)) {
      Constructor<? extends CustomBlock> constructor = createConstructor.get(nameID);
      try {
        customBlock = constructor.newInstance(location, player);
        if (directional.contains(nameID)) {
          customBlock.setDirection(direction);
        } else {
          customBlock.setDirection(BlockFace.NORTH);
        }
        customBlock.afterLoadUpdate();
      } catch (Exception e) {
        e.printStackTrace();
        sentryLog(e);
      }
      return customBlock;
    }
    Log.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }


}
