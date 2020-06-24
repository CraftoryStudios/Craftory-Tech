package tech.brettsaunders.craftory.api.blocks;


import lombok.Getter;
import org.bukkit.Location;
import tech.brettsaunders.craftory.persistence.Persistent;

@Getter
public class CustomBlock {

  @Persistent
  String blockName;
  Location location;

  public CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
  }

}
