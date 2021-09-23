/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GOneToOneMachine;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.Pair;

public class BaseOneToOneMachine extends BaseMachine implements IHopperInteract {

  protected static final int[] PROCESS_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  protected static final int INPUT_LOCATION = 21;
  protected static final int OUTPUT_LOCATION = 25;
  protected static final Map<BlockFace, Set<Integer>> inputFaces =
      new EnumMap<>(BlockFace.class);

  protected static final Map<BlockFace, Integer> outputFaces =
      new EnumMap<>(BlockFace.class);

  protected  Pair<String, String> currentRecipe = null;
  protected  ItemStack currentProduct = null;

  static {
    inputFaces.put(BlockFace.NORTH, Collections.singleton(INPUT_LOCATION));
    inputFaces.put(BlockFace.EAST, Collections.singleton(INPUT_LOCATION));
    inputFaces.put(BlockFace.SOUTH, Collections.singleton(INPUT_LOCATION));
    inputFaces.put(BlockFace.WEST, Collections.singleton(INPUT_LOCATION));
    inputFaces.put(BlockFace.UP, Collections.singleton(INPUT_LOCATION));

    outputFaces.put(BlockFace.DOWN, OUTPUT_LOCATION);
  }

  public BaseOneToOneMachine(Location location, String blockName, byte level, int maxRecieve) {
    super(location, blockName, level, maxRecieve);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    outputSlots = new ArrayList<>();
    outputSlots.add(new ItemStack(Material.AIR));
    processTime = PROCESS_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    setup();
  }

  public BaseOneToOneMachine() {
    super();
    setup();
  }

  private void setup() {
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
        new GOneToOneMachine(inventory, 23, progressContainer));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 30));
    if (inputSlots.isEmpty()) {
      inputSlots.add(0, new ItemStack(Material.AIR));
    }
    if (outputSlots.isEmpty()) {
      outputSlots.add(0, new ItemStack(Material.AIR));
    }
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

  protected HashMap<String, String> getRecipes() {
    Log.warn("THIS CODE SHOULD NEVER BE RUN");
    return null;
  }

  @Override
  protected boolean validateContentes() {
    if (inputSlots.get(0) == null || inputSlots.get(0).getType() == Material.AIR) {
      return false;
    }
    ItemStack outputSlot = outputSlots.get(0);
    String inputType = CustomItemManager.getCustomItemName(inputSlots.get(0));
    String outputType =
        (outputSlot == null) ? null : CustomItemManager.getCustomItemName(outputSlots.get(0));
    //If the recipe is unchanged there is no need to find the recipe.
    if (currentRecipe != null && currentRecipe.getX().equals(inputType)) {
      if (outputSlot == null || outputSlot.getType() == Material.AIR) {
        return true;
      }
      if (outputType.equals(currentRecipe.getY())
          && outputSlot.getAmount() <= outputSlot.getMaxStackSize() - 1) {
        return true;
      }
    }
    if (getRecipes().containsKey(inputType)) {
      String product;
      product = getRecipes().get(inputType);
      if ((outputSlot == null) || outputSlot.getType().equals(Material.AIR) || (
          outputType.equals(product) && outputSlot.getAmount() < outputSlot.getMaxStackSize())) {
        currentRecipe = new Pair<>(inputType, product);
        currentProduct = CustomItemManager.getCustomItemOrDefault(product);
        return true;
      }
    }
    currentRecipe = null;
    currentProduct = null;
    return false;
  }

  @Override
  public Map<BlockFace, Set<Integer>> getInputFaces() {
    return inputFaces;
  }

  @Override
  public Map<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }
}
