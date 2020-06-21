package tech.brettsaunders.craftory.tech.power.api.interfaces;

import java.util.HashMap;
import org.bukkit.block.BlockFace;

public interface IHopperInteract {
  HashMap<BlockFace,Integer> getInputFaces();
  HashMap<BlockFace,Integer> getOutputFaces();
}
