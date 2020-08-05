/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.CoreHolder.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public class PoweredBlockUtils {

  /**
   * Checks if a {@link CustomBlock} is an implementation of a PoweredBlock
   * @see PoweredBlock
   * @param block Custom block to check
   * @return true if instance of PoweredBlock
   */
  public static boolean isPoweredBlock(CustomBlock block) {
    return block instanceof PoweredBlock;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a PoweredBlock
   * Prefer use of {@link #isPoweredBlock(CustomBlock block)} over this
   * @see PoweredBlock
   * @param location of custom block to check
   * @return true if instance of PoweredBlock
   */
  public static boolean isPoweredBlock(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof PoweredBlock;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Cell
   * @see BaseCell
   * @param block Custom block to check
   * @return true if instance of Cell
   */
  public static boolean isCell(CustomBlock block) {
    return block instanceof BaseCell;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Generator
   * @see BaseGenerator
   * @param block Custom block to check
   * @return true if instance of Generator
   */
  public static boolean isGenerator(CustomBlock block) {
    return block instanceof BaseGenerator;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Machine
   * @see BaseMachine
   * @param block Custom block to check
   * @return true if instance of BaseMachine
   */
  public static boolean isMachine(CustomBlock block) {
    return block instanceof BaseMachine;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyReceiver
   * @see IEnergyReceiver
   * @param block Custom block to check
   * @return true if instance of IEnergyReceiver
   */
  public static boolean isEnergyReceiver(CustomBlock block) {
    return block instanceof IEnergyReceiver;
  }

  public static PoweredBlock getPoweredBlock(Location location) {
    return (PoweredBlock) Craftory.customBlockManager.getCustomBlock(location);
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyProvider
   * @see IEnergyProvider
   * @param block Custom block to check
   * @return true if instance of IEnergyProvider
   */
  public static boolean isEnergyProvider(CustomBlock block) {
    return block instanceof IEnergyProvider;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyProvider
   * Prefer use of {@link #isEnergyProvider(CustomBlock)} over this
   * @see IEnergyProvider
   * @param location of custom block to check
   * @return true if instance of IEnergyProvider
   */
  public static boolean isEnergyProvider(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof IEnergyProvider;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyReceiver
   * Prefer use of {@link #isEnergyReceiver(CustomBlock)} over this
   * @see IEnergyReceiver
   * @param location of custom block to check
   * @return true if instance of IEnergyReceiver
   */
  public static boolean isEnergyReceiver(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof IEnergyReceiver;
  }

  public static void updateAdjacentProviders(Location location, Boolean blockPlaced, CustomBlock originBlock) {
    Block block;
    Location blockLocation;
    for (BlockFace face : Utilities.faces) {
      block = location.getBlock().getRelative(face);
      blockLocation = block.getLocation();
      if (Craftory.customBlockManager.isCustomBlock(blockLocation)) {
        CustomBlock customBlock = Craftory.customBlockManager.getCustomBlock(blockLocation);
        if (isEnergyProvider(customBlock)) {
          PoweredBlock poweredBlock = (PoweredBlock) customBlock;
          poweredBlock.setSideCache(face.getOppositeFace(),
              (blockPlaced) ? INTERACTABLEBLOCK.RECEIVER : INTERACTABLEBLOCK.NONE, originBlock);
        } else if (blockPlaced && Craftory.customBlockManager.getCustomBlockName(blockLocation)
            == CoreHolder.Blocks.POWER_CONNECTOR) {
          if (isMachine(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location).addMachine(location, blockLocation);
          } else if (isGenerator(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location).addGenerator(location, blockLocation);
          } else if (isCell(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location).addPowerCell(location, blockLocation);
          }

        }
      }
    }
  }

  public static void updateHopperNeighbour(Block block, boolean hopperIsPresent) {
    BlockFace facingDirection = ((Directional) block.getBlockData()).getFacing();
    PoweredBlock poweredBlock = PoweredBlockUtils.getPoweredBlock((block.getRelative(facingDirection).getLocation()));
    if (poweredBlock != null) {
      poweredBlock.setSideCache(facingDirection.getOppositeFace(),
          (hopperIsPresent) ? INTERACTABLEBLOCK.HOPPER_IN
              : INTERACTABLEBLOCK.NONE);
    }
    poweredBlock = PoweredBlockUtils.getPoweredBlock(block.getRelative(BlockFace.UP).getLocation());
    if (poweredBlock != null) {
      poweredBlock.setSideCache(BlockFace.DOWN, (hopperIsPresent) ? INTERACTABLEBLOCK.HOPPER_OUT
          : INTERACTABLEBLOCK.NONE);
    }
  }
}
