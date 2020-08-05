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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.Pair;

public class BaseOneToOneMachine extends BaseMachine implements IHopperInteract {

  protected static final int[] PROCESS_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  protected static final int INPUT_LOCATION = 21;
  protected static final int OUTPUT_LOCATION = 25;
  protected static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, INPUT_LOCATION);
      put(BlockFace.EAST, INPUT_LOCATION);
      put(BlockFace.SOUTH, INPUT_LOCATION);
      put(BlockFace.WEST, INPUT_LOCATION);
      put(BlockFace.UP, INPUT_LOCATION);
    }
  };

  protected static final HashMap<BlockFace, Integer> outputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.DOWN, OUTPUT_LOCATION);
    }
  };

  protected transient Pair<String, String> currentRecipe = null;
  protected transient ItemStack currentProduct = null;

  public BaseOneToOneMachine(Location location, String blockName, byte level, int maxRecieve) {
    super(location, blockName, level, maxRecieve);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    outputSlots = new ArrayList<>();
    outputSlots.add(new ItemStack(Material.AIR));
    processTime = PROCESS_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    init();
  }

  public BaseOneToOneMachine(){
    super();
    init();
  }

  private void init() {
    inputLocations = new ArrayList<>();
    outputLocations = new ArrayList<>();
    inputLocations.add(INPUT_LOCATION);
    outputLocations.add(OUTPUT_LOCATION);
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION, OUTPUT_LOCATION));
  }
  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    processTime = PROCESS_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
  }



  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName,
        Font.FURNACE_GUI.label + "");
    addGUIComponent(
        new GOneToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 30));
    if (inputSlots.size() == 0) inputSlots.add(0, new ItemStack(Material.AIR));
    if (outputSlots.size() == 0) outputSlots.add(0, new ItemStack(Material.AIR));
    this.inventoryInterface = inventory;
  }

  @Override
  protected void processComplete() {
    inputSlots.get(0).setAmount(inputSlots.get(0).getAmount() - 1);
    if (outputSlots.get(0) == null || outputSlots.get(0).getType() == Material.AIR) {
      outputSlots.set(0, currentProduct);
    } else {
      outputSlots.get(0).setAmount(outputSlots.get(0).getAmount() + 1);
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots.get(0));
  }

  /* Internal Helper Functions */
  @Override
  protected void updateSlots() {
    inputSlots.set(0, inventoryInterface.getItem(INPUT_LOCATION));
    outputSlots.set(0, inventoryInterface.getItem(OUTPUT_LOCATION));
  }

  protected HashMap<String,String> getRecipes(){
    Logger.warn("THIS CODE SHOULD NEVER BE RUN");
    return null;
  }

  @Override
  protected boolean validateContentes() {
    if (inputSlots.get(0) == null || inputSlots.get(0).getType() == Material.AIR) {
      return false;
    }
    ItemStack outputSlot = outputSlots.get(0);
    String inputType = CustomItemManager.getCustomItemName(inputSlots.get(0));
    String outputType = (outputSlot==null) ? null : CustomItemManager.getCustomItemName(outputSlots.get(0));
    //If the recipe is unchanged there is no need to find the recipe.
    if (currentRecipe != null && currentRecipe.getX().equals(inputType)) {
      if (outputSlot == null || outputSlot.getType() == Material.AIR) {
        return true;
      }
      if (outputType.equals(currentRecipe.getY()) && outputSlot.getAmount() <= outputSlot.getMaxStackSize() - 1) {
        return true;
      }
    }
    if(getRecipes().containsKey(inputType)) {
      String product;
      product = getRecipes().get(inputType);
      if((outputSlot==null) || outputSlot.getType().equals(Material.AIR) || (outputType.equals(product) && outputSlot.getAmount() < outputSlot.getMaxStackSize())){
        currentRecipe = new Pair<>(inputType, product);
        if(CustomItemManager.isCustomItemName(product)) {
          currentProduct = CustomItemManager.getCustomItem(product);
        } else {
          currentProduct = new ItemStack(Material.valueOf(product));
        }
        return true;
      }
    }
    currentRecipe = null;
    currentProduct = null;
    return false;
  }
  @Override
  public HashMap<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }
  @Override
  public HashMap<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }
}
