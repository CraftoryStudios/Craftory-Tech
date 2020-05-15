package tech.brettsaunders.craftory.tech.power.api.interfaces;

import org.bukkit.block.BlockFace;

/**
 * Implement this interface on Tile Entities which should receive energy, generally storing it in one or more internal {@link IEnergyStorage} objects.
 */
public interface IEnergyReceiver extends IEnergyHandler {

  /**
   * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
   *
   * @param from       Orientation the energy is received from.
   * @param maxReceive Maximum amount of energy to receive.
   * @param simulate   If TRUE, the charge will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) received.
   */
  int receiveEnergy(BlockFace from, int maxReceive, boolean simulate);

  /**
   * Update all providers around block to inform them of new receiver
   */
  void updateNeighbourProviders();

}
