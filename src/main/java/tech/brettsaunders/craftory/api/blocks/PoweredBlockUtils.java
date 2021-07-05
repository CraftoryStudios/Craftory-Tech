/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import tech.brettsaunders.craftory.Constants;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public class PoweredBlockUtils {

  private PoweredBlockUtils() {
    throw new IllegalStateException("Utils Class");
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a PoweredBlock
   *
   * @param block Custom block to check
   * @return true if instance of PoweredBlock
   * @see PoweredBlock
   */
  public static boolean isPoweredBlock(CustomBlock block) {
    return block instanceof PoweredBlock;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a PoweredBlock Prefer use of {@link
   * #isPoweredBlock(CustomBlock block)} over this
   *
   * @param location of custom block to check
   * @return true if instance of PoweredBlock
   * @see PoweredBlock
   */
  public static boolean isPoweredBlock(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof PoweredBlock;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Cell
   *
   * @param block Custom block to check
   * @return true if instance of Cell
   * @see BaseCell
   */
  public static boolean isCell(CustomBlock block) {
    return block instanceof BaseCell;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Generator
   *
   * @param block Custom block to check
   * @return true if instance of Generator
   * @see BaseGenerator
   */
  public static boolean isGenerator(CustomBlock block) {
    return block instanceof BaseGenerator;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a Machine
   *
   * @param block Custom block to check
   * @return true if instance of BaseMachine
   * @see BaseMachine
   */
  public static boolean isMachine(CustomBlock block) {
    return block instanceof BaseMachine;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyReceiver
   *
   * @param block Custom block to check
   * @return true if instance of IEnergyReceiver
   * @see IEnergyReceiver
   */
  public static boolean isEnergyReceiver(CustomBlock block) {
    return block instanceof IEnergyReceiver;
  }

  public static PoweredBlock getPoweredBlock(Location location) {
    CustomBlock block = Craftory.customBlockManager.getCustomBlock(location);
    if(block instanceof PoweredBlock)
      return (PoweredBlock) block;
    return null;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyProvider
   *
   * @param block Custom block to check
   * @return true if instance of IEnergyProvider
   * @see IEnergyProvider
   */
  public static boolean isEnergyProvider(CustomBlock block) {
    return block instanceof IEnergyProvider;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyProvider Prefer use of {@link
   * #isEnergyProvider(CustomBlock)} over this
   *
   * @param location of custom block to check
   * @return true if instance of IEnergyProvider
   * @see IEnergyProvider
   */
  public static boolean isEnergyProvider(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof IEnergyProvider;
  }

  /**
   * Checks if a {@link CustomBlock} is an implementation of a IEnergyReceiver Prefer use of {@link
   * #isEnergyReceiver(CustomBlock)} over this
   *
   * @param location of custom block to check
   * @return true if instance of IEnergyReceiver
   * @see IEnergyReceiver
   */
  public static boolean isEnergyReceiver(Location location) {
    return Craftory.customBlockManager.getCustomBlock(location) instanceof IEnergyReceiver;
  }

  public static void updateAdjacentProviders(Location location, boolean blockPlaced,
      CustomBlock originBlock) {
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
        } else if (blockPlaced && Craftory.customBlockManager.getCustomBlockName(blockLocation).equals(Constants.Blocks.POWER_CONNECTOR)) {
          if (isMachine(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location)
                .addMachine(location, blockLocation);
          } else if (isGenerator(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location)
                .addGenerator(location, blockLocation);
          } else if (isCell(customBlock)) {
            Craftory.powerGridManager.getPowerGrids().get(location)
                .addPowerCell(location, blockLocation);
          }

        }
      }
    }
  }

  public static void updateHopperNeighbour(Block block, boolean hopperIsPresent) {
    BlockFace facingDirection = ((Directional) block.getBlockData()).getFacing();
    PoweredBlock poweredBlock = PoweredBlockUtils
        .getPoweredBlock((block.getRelative(facingDirection).getLocation()));
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
