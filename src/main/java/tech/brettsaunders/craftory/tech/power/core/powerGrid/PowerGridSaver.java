/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.powerGrid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashMap;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import tech.brettsaunders.craftory.persistence.Persistent;

@AllArgsConstructor
@NoArgsConstructor
public class PowerGridSaver {

  @Persistent
  protected Object2ObjectOpenHashMap<PowerGrid, ObjectOpenHashSet<Location>> data = new Object2ObjectOpenHashMap<>();

}
