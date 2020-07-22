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
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;

public class ItemStackAdapter implements DataAdapter<ItemStack> {

    @Override
    public void store(PersistenceStorage persistenceStorage, ItemStack value, NBTCompound nbtCompound) {
        nbtCompound.mergeCompound(NBTItem.convertItemtoNBT(value));
    }

    @Override
    public ItemStack parse(PersistenceStorage persistenceStorage, Object parentObject, NBTCompound nbtCompound) {
        return NBTItem.convertNBTtoItem(nbtCompound);
    }
}
