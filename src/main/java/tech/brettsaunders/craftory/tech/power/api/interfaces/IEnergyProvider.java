package tech.brettsaunders.craftory.tech.power.api.interfaces;

import org.bukkit.block.BlockFace;

/**
 * Implement this interface on Tile Entities which should provide energy, generally storing it in one or more internal {@link IEnergyStorage} objects.
 */
public interface IEnergyProvider extends IEnergyHandler {

  boolean updateOutputCache(BlockFace inputFrom, Boolean setTo);

}
