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
import tech.brettsaunders.craftory.utils.Pair;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseElectricFurnace extends BaseMachine implements IHopperInteract {

  /* Static Constants Protected */
  protected static final int[] COOKING_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final int INPUT_LOCATION = 21;
  private static final int OUTPUT_LOCATION = 25;
  private static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, INPUT_LOCATION);
      put(BlockFace.EAST, INPUT_LOCATION);
      put(BlockFace.SOUTH, INPUT_LOCATION);
      put(BlockFace.WEST, INPUT_LOCATION);
      put(BlockFace.UP, INPUT_LOCATION);
    }
  };

  private static final HashMap<BlockFace, Integer> outputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.DOWN, OUTPUT_LOCATION);
    }
  };

  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient Pair<String, String> currentRecipe = null;
  private transient ItemStack currentProduct = null;


  /* Construction */
  public BaseElectricFurnace(Location location, String blockName, byte level) {
    super(location, blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    processTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    outputSlots = new ArrayList<>();
    outputSlots.add(new ItemStack(Material.AIR));
    inputLocations.add(INPUT_LOCATION);
    outputLocations.add(OUTPUT_LOCATION);
  }

  /* Saving, Setup and Loading */
  public BaseElectricFurnace() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION, OUTPUT_LOCATION));
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    processTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle(displayName,
        Font.FURNACE_GUI.label + "");
    addGUIComponent(
        new GOneToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 30));
    if (inputSlots.size() == 0) inputSlots.add(0, new ItemStack(Material.AIR));
    if (outputSlots.size() == 0) outputSlots.add(0, new ItemStack(Material.AIR));
    inventory.setItem(INPUT_LOCATION, inputSlots.get(0));
    inventory.setItem(OUTPUT_LOCATION, outputSlots.get(0));
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
    if(RecipeUtils.getFurnaceRecipes().containsKey(inputType)) {
      String product;
      product = RecipeUtils.getFurnaceRecipes().get(inputType);
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
