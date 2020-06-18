package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseElectricFurnace extends BaseMachine implements Externalizable {

  /* Static Constants Protected */
  protected static final int[] COOKING_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final long serialVersionUID = 10005L;
  private static final int INPUT_LOCATION = 21;
  private static final int OUTPUT_LOCATION = 25;
  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient FurnaceRecipe currentRecipe = null;


  /* Construction */
  public BaseElectricFurnace(Location location, byte level) {
    super(location, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ItemStack[]{null};
    outputSlots = new ItemStack[]{null};
    inputLocations.add(INPUT_LOCATION);
    outputLocations.add(OUTPUT_LOCATION);
    setupGUI();
  }

  /* Saving, Setup and Loading */
  public BaseElectricFurnace() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    processTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION, OUTPUT_LOCATION));
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    updateSlots();
    out.writeObject(energyStorage);

  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    energyStorage = (EnergyStorage) in.readObject();
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Electric Furnace",
        Font.FURNACE_GUI.label + ""); //TODO Furnance
    addGUIComponent(
        new GOneToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 30));
    inventory.setItem(INPUT_LOCATION, inputSlots[0]);
    inventory.setItem(OUTPUT_LOCATION, outputSlots[0]);
    this.inventoryInterface = inventory;
  }


  @Override
  protected void processComplete() {
    inputSlots[0].setAmount(inputSlots[0].getAmount() - 1);
    if (outputSlots[0] == null) {
      outputSlots[0] = currentRecipe.getResult();
    } else {
      outputSlots[0].setAmount(outputSlots[0].getAmount() + currentRecipe.getResult().getAmount());
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots[0]);
  }

  /* Internal Helper Functions */
  @Override
  protected void updateSlots() {
    inputSlots[0] = inventoryInterface.getItem(INPUT_LOCATION);
    outputSlots[0] = inventoryInterface.getItem(OUTPUT_LOCATION);
  }


  @Override
  protected boolean validateContentes() {
    if (inputSlots[0] == null) {
      return false;
    }
    String inputType = CustomItemManager.getCustomItemName(inputSlots[0]);
    //If the recipe is unchanged there is no need to find the recipe.
    if (currentRecipe != null && currentRecipe.getInput().getType().toString().equals(inputType)) {
      if (outputSlots[0] == null) {
        return true;
      }
      if (outputSlots[0].getType().toString().equals(currentRecipe.getResult().getType().toString())
          && outputSlots[0].getAmount() < outputSlots[0].getMaxStackSize()) {
        return true;
      }
    }
    FurnaceRecipe furnaceRecipe;
    for (Recipe recipe : RecipeUtils.getFurnaceRecipes()) {
      furnaceRecipe = (FurnaceRecipe) recipe;
      if (!furnaceRecipe.getInput().getType().toString().equals(inputType)) {
        continue;
      }
      currentRecipe = furnaceRecipe;
      if (outputSlots[0] == null) {
        return true;
      }
      if (CustomItemManager.getCustomItemName(outputSlots[0])
          .equals(recipe.getResult().getType().toString())
          && outputSlots[0].getAmount() < outputSlots[0].getMaxStackSize()) {
        return true;
      }
    }
    currentRecipe = null;
    return false;
  }

}
