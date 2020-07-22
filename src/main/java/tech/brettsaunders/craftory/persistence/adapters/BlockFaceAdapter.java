/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.persistence.adapters;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class BlockFaceAdapter implements DataAdapter<BlockFace> {

    @Override
    public void store(PersistenceStorage persistenceStorage, BlockFace value, NBTCompound nbtCompound) {
        nbtCompound.setString("blockFace", value.name());
    }

    @Override
    public BlockFace parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        if (!nbtCompound.hasKey("blockFace")) {
            return null;
        }
        return BlockFace.valueOf(nbtCompound.getString("blockFace"));
    }
}
