package tech.brettsaunders.craftory.api.blocks;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Synchronized;
import org.bukkit.Location;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.basicBlocks.PowerConnector;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomBlockFactory {
  private HashMap<String, Class<? extends CustomBlock>> blockTypes = new HashMap<>();
  private HashMap<String, Constructor<? extends CustomBlock>> createConstructor = new HashMap<>();
  private HashMap<String, Constructor<? extends CustomBlock>> loadConstructor = new HashMap<>();
  private String locationName;
  private StatsContainer statsContainer;

  public CustomBlockFactory() {
    locationName = Location.class.getName();
  }

  @Synchronized
  public void registerCustomBlock(String nameID, Class<? extends CustomBlock> block) {
    blockTypes.put(nameID, block);
    Constructor[] constructors = block.getConstructors();
    for (Constructor constructor : constructors) {
      if (constructor.getParameterCount() == 0) {
        loadConstructor.put(nameID, constructor);
      } else if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].getName().equals(locationName)){
        createConstructor.put(nameID, constructor);
      }
    }
  }

  @Synchronized
  public boolean isCustomBlockRegistered(CustomBlock block) {
    return blockTypes.containsKey(block.getClass());
  }

  @Synchronized
  public String getKey(CustomBlock block) {
    return getKey(block.getClass());
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
  public CustomBlock createLoad(NBTCompound locationCompound, PersistenceStorage persistenceStorage, Location location) {
    CustomBlock customBlock = null;
    NBTCompound nameCompound = locationCompound.getCompound("blockName");
    String nameID = nameCompound.getString("data");
    if (loadConstructor.containsKey(nameID)) {
      Constructor constructor = loadConstructor.get(nameID);
      try {
        customBlock = (CustomBlock) constructor.newInstance();
        persistenceStorage.loadFields(customBlock,locationCompound);
        customBlock.setLocation(location);
        customBlock.afterLoadUpdate();
        calculateStatsIncrease(customBlock);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return customBlock;
    }
    Logger.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }

  @Synchronized
  public CustomBlock create(String nameID, Location location) {
    CustomBlock customBlock = null;
    if (createConstructor.containsKey(nameID)) {
      Constructor constructor = createConstructor.get(nameID);
      try {
        customBlock = (CustomBlock) constructor.newInstance(location);
        customBlock.afterLoadUpdate();
        calculateStatsIncrease(customBlock);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return customBlock;
    }
    Logger.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }

  public void registerStats() {
    statsContainer = Craftory.customBlockManager.statsContainer;
  }

  private void calculateStatsIncrease(CustomBlock customBlock) {
    statsContainer.increaseTotalCustomBlocks();
    if (customBlock instanceof PoweredBlock) {
      statsContainer.increaseTotalPoweredBlocks();
      if (customBlock instanceof BaseMachine) {
        statsContainer.increaseTotalMachines();
      } else if (customBlock instanceof BaseCell) {
        statsContainer.increaseTotalCells();
      } else if (customBlock instanceof BaseGenerator) {
        statsContainer.increaseTotalGenerators();
      } else if (customBlock instanceof PowerConnector) {
        statsContainer.increaseTotalPowerConnectors();
      }
    }
    Logger.info(statsContainer.getTotalCustomBlocks()+"");
  }

}
