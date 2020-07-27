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

/**
 * Implement this interface on TileEntities which should connect to energy transportation blocks.
 * This is intended for blocks which generate energy but do not accept it; otherwise just use
 * IEnergyHandler.
 * <p>
 * Note that {@link IEnergyHandler} is an extension of this.
 */
public interface IEnergyConnection {

  /**
   * Returns TRUE if the TileEntity can connect on a given side.
   */
  boolean canConnectEnergy();

}
