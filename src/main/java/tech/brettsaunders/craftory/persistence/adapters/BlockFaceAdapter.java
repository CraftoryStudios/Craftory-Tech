/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class BlockFaceAdapter implements DataAdapter<BlockFace> {

  public static final String BLOCK_FACE = "blockFace";

  @Override
  public void store(PersistenceStorage persistenceStorage, BlockFace value,
      NBTCompound nbtCompound) {
    nbtCompound.setString(BLOCK_FACE, value.name());
  }

  @Override
  public BlockFace parse(PersistenceStorage persistenceStorage, Object parentObject,
      NBTCompound nbtCompound) {
    if (!Boolean.TRUE.equals(nbtCompound.hasKey(BLOCK_FACE))) {
      return null;
    }
    return BlockFace.valueOf(nbtCompound.getString(BLOCK_FACE));
  }
}
