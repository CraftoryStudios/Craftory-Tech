/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.interfaces;

/**
 * Implement this interface on objects which can report information about their energy usage.
 * <p>
 * This is used for reporting purposes - Energy transactions are handled via the RE API!
 */

public interface IEnergyInfo {

  /**
   * Returns energy usage/generation per tick (Power/t).
   */
  int getInfoEnergyPerTick();

  /**
   * Returns maximum energy usage/generation per tick (Power/t).
   */
  int getInfoMaxEnergyPerTick();

  /**
   * Returns energy stored (Power).
   */
  int getInfoEnergyStored();


}
