/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.interfaces;

import java.util.Map;
import org.bukkit.block.BlockFace;

public interface IHopperInteract {

  Map<BlockFace, Integer> getInputFaces();

  Integer getOutputSlot();
}
