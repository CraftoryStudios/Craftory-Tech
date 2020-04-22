package tech.brettsaunders.craftory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DataContainer implements Serializable {

  private static transient final long serialVersionUID = -1681012206529286330L;

  //Data to Save
  public final HashSet<Long> chunkKeys;
  public final HashMap<Location, BeltManager> beltManagers;
  public final HashSet<Block> earths;
  public final HashSet<Block> closedList;

  //Used for saving data
  public DataContainer(HashSet<Long> chunkKeys, HashMap<Location, BeltManager> beltManagers, HashSet<Block> earths, HashSet<Block> closedList) {
    this.chunkKeys = chunkKeys;
    this.beltManagers = beltManagers;
    this.earths = earths;
    this.closedList = closedList;
  }

  //Used for loading data
  public DataContainer(DataContainer loadedData) {
    if (loadedData == null) {
      this.chunkKeys = null;
      this.beltManagers = null;
      this.earths = null;
      this.closedList = null;
    } else {
      this.chunkKeys = loadedData.chunkKeys;
      this.beltManagers = loadedData.beltManagers;
      this.earths = loadedData.earths;
      this.closedList = loadedData.closedList;
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
      HashMap<Location, BeltManager> beltManagers, HashSet<Block> earths, HashSet<Block> closedList) {
    new DataContainer(chunkKeys, beltManagers, earths, closedList).saveData("Carftory.data");
  }

  public static DataContainer loadData() {
    return new DataContainer(DataContainer.loadData("Carftory.data"));
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
