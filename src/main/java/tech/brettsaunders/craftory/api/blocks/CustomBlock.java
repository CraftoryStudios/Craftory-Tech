package tech.brettsaunders.craftory.api.blocks;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;

public class CustomBlock {
  String blockName;
  Location location;

  public CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
  }

  public void writeDataFile(NBTCompound nbtCompound) {
    nbtCompound.setString("BLOCK_NAME", blockName);
  }
}
