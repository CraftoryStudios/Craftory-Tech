/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;


import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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
  @Persistent
  @Setter
  protected BlockFace direction;

  protected CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
    this.displayName = Utilities.getTranslation(blockName);
    this.direction = BlockFace.NORTH;
  }

  public void afterLoadUpdate() {
    //TODO Remove in future version
    if (Strings.isNullOrEmpty(displayName)) {
      displayName = Utilities.getTranslation(blockName);
    }

    if (direction == null) {
      direction = BlockFace.NORTH;
    }
  }

  public void beforeSaveUpdate() {
    //Override to add functionality
  }

  public void blockBreak() {
    //Override to add functionality
  }

}
