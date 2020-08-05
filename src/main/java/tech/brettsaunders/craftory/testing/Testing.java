/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.testing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;

public class Testing {

  public static void placeGrid(Location baseLocation, String mainMachineName, String generatorName, String energyCellName, String insertItemName, String fuelName, int length, int width) {
    Location location = baseLocation.clone();
    for (int i = 0; i < width; i++) {
      placeColumn(location, mainMachineName, generatorName, energyCellName, insertItemName, fuelName, length);
      location.add(0,0,5);
    }
  }

  public static void placeColumn(Location baseLocation, String mainMachineName, String generatorName, String energyCellName, String insertItemName, String fuelName, int length) {
    Location location = baseLocation.clone();
    for (int i = 0; i < length; i++) {
      placeModule(location, mainMachineName, generatorName, energyCellName, insertItemName, fuelName);
      location.add(2,0,0);
    }
  }

  private static void placeModule(Location baseLocation, String mainMachineName, String generatorName, String energyCellName, String insertItemName, String fuelName) {
    baseLocation.clone().add(0,2,2).getBlock().setType(Material.REDSTONE_BLOCK);
    baseLocation.clone().add(0,0,1).getBlock().setType(Material.HOPPER);

    //Machine Hooper
    Block hopperMachine = baseLocation.clone().add(0, 2, 1).getBlock();
    hopperMachine.setType(Material.HOPPER);
    ItemStack[] machineHopperContents = ((Hopper) hopperMachine.getState()).getInventory().getContents();
    int size = ((Hopper) hopperMachine.getState()).getInventory().getSize();
    ItemStack insertItem;
    if (CustomItemManager.isCustomItemName(insertItemName)) {
      insertItem = CustomItemManager.getCustomItem(insertItemName);
    } else {
      insertItem = new ItemStack(Material.getMaterial(insertItemName));
    }
    insertItem.setAmount(64);

    for (int i = 0; i < size; i++) {
      ((Hopper) hopperMachine.getState()).getInventory().setItem(i, insertItem.clone());
    }

    //Generator Hopper
    Block hopperGenerator = baseLocation.clone().add(0, 2, 3).getBlock();
    hopperGenerator.setType(Material.HOPPER);
    ItemStack[] generatorHopperContents = ((Hopper) hopperGenerator.getState()).getInventory().getContents();
    ItemStack fuelItem;
    if (CustomItemManager.isCustomItemName(fuelName)) {
      fuelItem = CustomItemManager.getCustomItem(fuelName);
    } else {
      fuelItem = new ItemStack(Material.getMaterial(fuelName));
    }
    fuelItem.setAmount(64);

    for (int i = 0; i < size; i++) {
      ((Hopper) hopperGenerator.getState()).getInventory().setItem(i, fuelItem.clone());
    }


    BaseProvider energyCellBlock = (BaseProvider) placeBlock(energyCellName, baseLocation.clone().add(0,1,2));
    BaseProvider generatorBlock = (BaseProvider) placeBlock(generatorName, baseLocation.clone().add(0,1,3));
    PoweredBlock machineBlock = (PoweredBlock) placeBlock(mainMachineName, baseLocation.clone().add(0,1,1));
    energyCellBlock.setSideConfigSide(BlockFace.NORTH,true);
    generatorBlock.setSideConfigSide(BlockFace.NORTH, true);
  }

  private static CustomBlock placeBlock(String blockName, Location location) {
    CustomBlock customBlock = Craftory.customBlockManager.placeCustomBlock(blockName, location.getBlock(), BlockFace.NORTH);
    CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
        location, blockName, location.getBlock(), customBlock);
    Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);
    return customBlock;
  }

}
