/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
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

  public CustomBlock(Location location, String blockName) {
    this.location = location;
    this.blockName = blockName;
    this.displayName = Utilities.langProperties.getProperty(blockName);
    this.direction = BlockFace.NORTH;
  }

  public void afterLoadUpdate() {
    //TODO Remove in future version
    if (Strings.isNullOrEmpty(displayName)) {
      displayName = Utilities.langProperties.getProperty(blockName);
    }

    if (direction == null) direction = BlockFace.NORTH;
  }

  public void beforeSaveUpdate() {}

  public void blockBreak() {}

}
