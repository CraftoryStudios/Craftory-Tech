/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.interfaces;

/**
 * Implement this interface on Tile Entities which should handle energy, generally storing it in one
 * or more internal {@link IEnergyStorage} objects.
 * <p>
 * Note that {@link IEnergyReceiver} and {@link IEnergyProvider} are extensions of this.
 */
public interface IEnergyHandler extends IEnergyConnection {

  /**
   * Returns the amount of energy currently stored.
   */
  int getEnergyStored();

  /**
   * Returns the maximum amount of energy that can be stored.
   */
  int getMaxEnergyStored();

}
