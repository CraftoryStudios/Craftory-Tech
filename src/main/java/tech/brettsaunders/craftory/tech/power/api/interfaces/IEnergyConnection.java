package tech.brettsaunders.craftory.tech.power.api.interfaces;

/**
 * Implement this interface on TileEntities which should connect to energy transportation blocks. This is intended for blocks which generate energy but do not
 * accept it; otherwise just use IEnergyHandler.
 *
 * Note that {@link IEnergyHandler} is an extension of this.
 *
 */
public interface IEnergyConnection {

  /**
   * Returns TRUE if the TileEntity can connect on a given side.
   */
  boolean canConnectEnergy();

}
