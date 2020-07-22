/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.interfaces;

import java.util.HashMap;
import org.bukkit.block.BlockFace;

public interface IHopperInteract {

  HashMap<BlockFace, Integer> getInputFaces();

  HashMap<BlockFace, Integer> getOutputFaces();
}
