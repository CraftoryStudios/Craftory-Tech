/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.interfaces;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import org.bukkit.block.BlockFace;

public interface IHopperInteract {

  Object2ObjectOpenHashMap<BlockFace, Integer> getInputFaces();

  Object2ObjectOpenHashMap<BlockFace, Integer> getOutputFaces();
}
