package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GTwoToOneMachine;
import tech.brettsaunders.craftory.utils.Items;
import tech.brettsaunders.craftory.utils.Items.Components;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseFoundry extends BaseMachine implements Externalizable {
  /* Static Constants Protected */
  protected static final int[] PROCESSING_TIME_LEVEL = {400, 300, 200, 100}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final long serialVersionUID = 10023L;
  private static final int INPUT_LOCATION1 = 21;
  private static final int INPUT_LOCATION2 = 22;
  private static final int OUTPUT_LOCATION = 26;
  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient FurnaceRecipe currentRecipe = null;



  /* Construction */
  public BaseFoundry(Location location, byte level) {
    super(location, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ItemStack[]{null,null};
    outputSlots = new ItemStack[]{null};
    inputLocations = new int[]{INPUT_LOCATION1, INPUT_LOCATION2};
    outputLocations = new int[]{OUTPUT_LOCATION};
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
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION1,INPUT_LOCATION2, OUTPUT_LOCATION));
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
    Inventory inventory = setInterfaceTitle("Foundry", new FontImageWrapper("extra:cell"));
    addGUIComponent(
        new GTwoToOneMachine(inventory, 24, progressContainer, INPUT_LOCATION1,INPUT_LOCATION2, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer));
    inventory.setItem(INPUT_LOCATION1, inputSlots[0]);
    inventory.setItem(INPUT_LOCATION2, inputSlots[1]);
    inventory.setItem(OUTPUT_LOCATION, outputSlots[0]);
    this.inventoryInterface = inventory;
    HashSet<Integer> protectedslots = new HashSet<>();
    protectedslots.add(INPUT_LOCATION1);
    protectedslots.add(INPUT_LOCATION2);
    protectedslots.add(OUTPUT_LOCATION);
    fillBlankSlots(protectedslots);
  }


  @Override
  protected void processComplete(){
    inputSlots[0].setAmount(inputSlots[0].getAmount() - 1);
    inputSlots[1].setAmount(inputSlots[1].getAmount() - 1);
    if (outputSlots[0] == null) {
      outputSlots[0] = ItemsAdder.getCustomItem(Components.STEEL_INGOT);
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
  protected boolean validateContense() {
    if (inputSlots[0] == null || inputSlots[1] ==null) {
      return false;
    }
    String inputType1 = inputSlots[0].getType().toString();
    String inputType2 = inputSlots[1].getType().toString();
    if (outputSlots == null) {
      Logger.info("wtf");
    }
    if(outputSlots[0]!=null){
      String outputType = ItemsAdder.getCustomItemName(outputSlots[0]);
      if (!outputType.equals(Components.STEEL_INGOT) || outputSlots[0].getAmount() == outputSlots[0].getMaxStackSize()) return false;
    }
    return (inputType1.equals(Material.IRON_ORE.toString()) && inputType2.equals(Material.COAL.toString())) ||
        (inputType1.equals(Material.COAL.toString()) && inputType2.equals(Material.IRON_ORE.toString()));
  }
}
