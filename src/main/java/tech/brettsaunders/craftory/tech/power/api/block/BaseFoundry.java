package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GTwoToOneMachine;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.RecipeUtils.CustomMachineRecipe;

public class BaseFoundry extends BaseMachine implements Externalizable {

  /* Static Constants Protected */
  protected static final int[] PROCESSING_TIME_LEVEL = {400, 300, 200, 100}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final long serialVersionUID = 10023L;
  private static final int INPUT_LOCATION1 = 12;
  private static final int INPUT_LOCATION2 = 30;
  private static final int OUTPUT_LOCATION = 25;
  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient CustomMachineRecipe currentRecipe = null;


  /* Construction */
  public BaseFoundry(Location location, byte level) {
    super(location, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ItemStack[]{null, null};
    outputSlots = new ItemStack[]{null};
    inputLocations.add(INPUT_LOCATION1);
    inputLocations.add(INPUT_LOCATION2);
    outputLocations.add(OUTPUT_LOCATION);
    if (ItemsAdder.areItemsLoaded()) {
      setupGUI();
    }
  }

  /* Saving, Setup and Loading */
  public BaseFoundry() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    processTime = PROCESSING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    interactableSlots = new HashSet<>(
        Arrays.asList(INPUT_LOCATION1, INPUT_LOCATION2, OUTPUT_LOCATION));
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
    Inventory inventory = setInterfaceTitle("Foundry", new FontImageWrapper("extra:foundry"));
    addGUIComponent(
        new GTwoToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION1, INPUT_LOCATION2,
            OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer,21));
    inventory.setItem(INPUT_LOCATION1, inputSlots[0]);
    inventory.setItem(INPUT_LOCATION2, inputSlots[1]);
    inventory.setItem(OUTPUT_LOCATION, outputSlots[0]);
    this.inventoryInterface = inventory;
  }


  @Override
  protected void processComplete() {
    inputSlots[0].setAmount(inputSlots[0].getAmount() - 1);
    inputSlots[1].setAmount(inputSlots[1].getAmount() - 1);
    if (outputSlots[0] == null) {
      outputSlots[0] = ItemsAdder.getCustomItem(CoreHolder.Items.STEEL_INGOT);
    } else {
      outputSlots[0].setAmount(outputSlots[0].getAmount() + 1);
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots[0]);
  }

  /* Internal Helper Functions */
  @Override
  protected void updateSlots() {
    inputSlots[0] = inventoryInterface.getItem(INPUT_LOCATION1);
    inputSlots[1] = inventoryInterface.getItem(INPUT_LOCATION2);
    outputSlots[0] = inventoryInterface.getItem(OUTPUT_LOCATION);
  }


  @Override
  protected boolean validateContentes() {
    if (inputSlots[0] == null || inputSlots[1] == null) {
      return false;
    }
    String inputType1 = CoreHolder.getItemName(inputSlots[0]);
    String inputType2 = CoreHolder.getItemName(inputSlots[1]);
    int inputAmount1 = inputSlots[0].getAmount();
    int inputAmount2 = inputSlots[1].getAmount();
    String outputType = null;
    if (outputSlots[0] != null) {
      outputType = CoreHolder.getItemName(outputSlots[0]);
    }
    //If the recipe is unchanged there is no need to find the recipe.

    if (currentRecipe != null) {
      boolean valid = true;
      for (Map.Entry<String, Integer> entry : currentRecipe.getIngredients().entrySet()) {
        String item = entry.getKey();
        int number = entry.getValue();
        if (!((item.equals(inputType1) && inputAmount1 >= number) || (item.equals(inputType2)
            && inputAmount2 >= number))) {
          valid = false;
          break;
        }
      }
      if (valid && outputSlots[0] != null) {
        if (currentRecipe.getProducts().containsKey(outputType)
            && (outputSlots[0].getAmount() + currentRecipe.getProducts().get(outputType))
            <= outputSlots[0].getMaxStackSize()) {
          return true;
        }
      }
    }
    for (CustomMachineRecipe recipe : RecipeUtils.getTwoToOneRecipes()) {
      boolean valid = true;
      for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
        String item = entry.getKey();
        int number = entry.getValue();
        if (!((item.equals(inputType1) && inputAmount1 >= number) || (item.equals(inputType2)
            && inputAmount2 >= number))) {
          valid = false;
          break;
        }
      }
      if (valid) {
        currentRecipe = recipe;
        if (outputSlots[0] == null || (currentRecipe.getProducts().containsKey(outputType)
            && (outputSlots[0].getAmount() + currentRecipe.getProducts().get(outputType))
            <= outputSlots[0].getMaxStackSize())) {
          return true;
        }
      }
    }
    currentRecipe = null;
    return false;
  }
}
