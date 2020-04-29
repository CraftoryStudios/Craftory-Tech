package tech.brettsaunders.craftory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.tech.belts.BeltManager;

public class DataContainer implements Serializable {

  private static transient final long serialVersionUID = -1681012206529286330L;

  //Data to Save
  public final HashSet<Long> chunkKeys;
  public final HashMap<Location, BeltManager> beltManagers;

  //Used for saving data
  public DataContainer(HashSet<Long> chunkKeys, HashMap<Location, BeltManager> beltManagers) {
    this.chunkKeys = chunkKeys;
    this.beltManagers = beltManagers;
  }

  //Used for loading data
  public DataContainer(DataContainer loadedData) {
    if (loadedData == null) {
      this.chunkKeys = null;
      this.beltManagers = null;
    } else {
      this.chunkKeys = loadedData.chunkKeys;
      this.beltManagers = loadedData.beltManagers;
    }
  }

  public static DataContainer loadData(String filePath) {
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(filePath)));
      DataContainer data = (DataContainer) in.readObject();
      in.close();
      return data;
    } catch (ClassNotFoundException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  public static void saveData(HashSet<Long> chunkKeys,
      HashMap<Location, BeltManager> beltManagers) {
    new DataContainer(chunkKeys, beltManagers).saveData(Craftory.getInstance().getDataFolder() + File
        .separator + "craftory.data");
  }

  public static DataContainer loadData() {
    return new DataContainer(DataContainer.loadData(Craftory.getInstance().getDataFolder() + File
        .separator +"craftory.data"));
  }

  private boolean saveData(String filePath) {
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(filePath)));
      out.writeObject(this);
      out.close();
      return true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }
}