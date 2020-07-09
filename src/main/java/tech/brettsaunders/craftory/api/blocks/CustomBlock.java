package tech.brettsaunders.craftory.api.blocks;


import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.persistence.Persistent;

@NoArgsConstructor
@Getter
public class CustomBlock {

  @Persistent
  protected String blockName;
  @Persistent
  protected String displayName;
  @Setter
  @Persistent
  protected Location location;

  public CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
    this.displayName = Utilities.langProperties.getProperty(blockName);
  }

  public void afterLoadUpdate() {
    //TODO Remove in future version
    if (Strings.isNullOrEmpty(displayName)) {
      displayName = Utilities.langProperties.getProperty(blockName);
    }
  }

  public void beforeSaveUpdate() {}

}
