package tech.brettsaunders.craftory.api.blocks;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import tech.brettsaunders.craftory.persistence.Persistent;

@NoArgsConstructor
@Getter
public class CustomBlock {

  @Persistent
  protected String blockName;
  @Persistent
  protected Location location;

  public CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
  }

}
