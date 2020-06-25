package tech.brettsaunders.craftory.api.blocks;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Synchronized;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.utils.Logger;

public class CustomBlockFactory {
  private HashMap<String, Class<? extends CustomBlock>> blockTypes = new HashMap<>();
  private HashMap<String, Constructor<? extends CustomBlock>> createConstructor = new HashMap<>();
  private HashMap<String, Constructor<? extends CustomBlock>> loadConstructor = new HashMap<>();
  private String locationName;

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

  public CustomBlock createLoad(NBTCompound locationCompound, PersistenceStorage persistenceStorage) {
    CustomBlock customBlock = null;
    String nameID = locationCompound.getString("blockName");
    if (loadConstructor.containsKey(nameID)) {
      Constructor constructor = loadConstructor.get(nameID);
      try {
        customBlock = (CustomBlock) constructor.newInstance();
        persistenceStorage.loadFields(customBlock,locationCompound);
        customBlock.afterLoadUpdate();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return customBlock;
    }
    Logger.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }

  public CustomBlock create(String nameID, Location location) {
    CustomBlock customBlock = null;
    if (createConstructor.containsKey(nameID)) {
      Constructor constructor = createConstructor.get(nameID);
      try {
        customBlock = (CustomBlock) constructor.newInstance(location);
        customBlock.afterLoadUpdate();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return customBlock;
    }
    Logger.error("No Custom Block Class found of type " + nameID);
    return customBlock;
  }

}
