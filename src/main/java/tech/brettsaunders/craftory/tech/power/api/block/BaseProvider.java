/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;

public abstract class BaseProvider extends PoweredBlock implements IEnergyProvider {

  /* Per Object Variables Saved */
  @Persistent
  protected int maxOutput;
  @Persistent
  protected HashMap<BlockFace, Boolean> sidesConfig;

  /* Construction */
  public BaseProvider(Location location, String blockName, byte level, int maxOutput) {
    super(location, blockName, level);
    this.maxOutput = maxOutput;
    init();
    for (BlockFace face : Utilities.faces) {
      sidesConfig.put(face,false);
    }
  }

  /* Saving, Setup and Loading */
  public BaseProvider() {
    super();
    init();
  }

  /* Common Load and Construction */
  private void init() {
    sidesConfig = new HashMap<>(6);
  }

  /* Update Loop */
  @Ticking(ticks = 1)
  public void transferEnergy() {
    if (isBlockPowered()) return;
    cachedSides.forEach(((blockFace, customBlock) -> {
      if (customBlock == null) {
        Bukkit.getScheduler().runTaskLater(Craftory.plugin, new Runnable() {
          @Override
          public void run() {
            CustomBlock sideBlock = Craftory.customBlockManager.getCustomBlock(location.getBlock().getRelative(blockFace).getLocation());
            if (sideBlock != null) {
              cachedSides.put(blockFace, sideBlock);
            }
          }
        },4);
      }
      if (sidesConfig.get(blockFace)) {
        energyStorage.modifyEnergyStored(-insertEnergyIntoAdjacentEnergyReceiver(Math.min(maxOutput, energyStorage.getEnergyStored()), false, customBlock));
      }
    }));
  }

  public int retrieveEnergy(int energy) {
    int energyExtracted = Math.min(getEnergyStored(), Math.min(energy, maxOutput));
    energyStorage.modifyEnergyStored(-energyExtracted);
    return energyExtracted;
  }

  public void setSideConfigSide(BlockFace side,  boolean result) {
    sidesConfig.put(side, result);
  }


  /* Internal Helper Functions */


  public int insertEnergyIntoAdjacentEnergyReceiver(int energy, boolean simulate, CustomBlock customBlock) {
    if (customBlock instanceof BaseMachine) {
      return ((BaseMachine) customBlock).receiveEnergy(energy, simulate);
    } else if (customBlock instanceof BaseCell) {
      return ((BaseCell) customBlock).receiveEnergy(energy, simulate);
    }
    return 0;
  }


  public HashMap<BlockFace, Boolean> getSideConfig() {
    return sidesConfig;
  }

  public void setSidesConfig(HashMap<BlockFace, Boolean> config) {
    sidesConfig.clear();
    sidesConfig.putAll(config);
  }

  /* IEnergyHandler */
  @Override
  public int getEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return energyStorage.getMaxEnergyStored();
  }

  /* IEnergyInfo */
  @Override
  public int getInfoMaxEnergyPerTick() {
    return maxOutput;
  }

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int getMaxOutput() {
    return maxOutput;
  }

  public int getEnergyAvailable() {
    return Math.min(energyStorage.energy, maxOutput);
  }
}
