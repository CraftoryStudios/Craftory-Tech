package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseElectricFurnace extends BaseMachine{

  //Normal MC furnace takes 200 ticks to smelt an item
  protected static final int[] COOKING_TIME_LEVEL = {200,150,100,50};
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20,30,50,100};
  protected static final int[] CAPACITY_LEVEL = { 5000, 10000, 25000, 50000};
  private static final int INPUT_LOCATION = 22;
  private static final int OUTPUT_LOCATION = 26;

  private ItemStack inputSlot;
  private ItemStack outputSlot;
  private Inventory inventory;
  private int cookingTime;
  private int energyConsumption;
  private int tickCount = 0;
  private FurnaceRecipe currentRecipe = null;
  private VariableContainer<Boolean> runningContainer;
  private VariableContainer<Double> progressContainer;

  public BaseElectricFurnace(Location location, byte level) {
    super(location, level);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    energyStorage.maxReceive = ENERGY_CONSUMPTION_LEVEL[level] * 5;
    addGUIComponent(new GOneToOneMachine(getInventory(), 24, progressContainer));
    addGUIComponent(new GBattery(getInventory(), energyStorage));
    addGUIComponent(new GIndicator(getInventory(), runningContainer));
  }

  public BaseElectricFurnace() {
    super();
    init();
  }

  /* Shared Startup and Loading */
  public void init() {
    cookingTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    inventory = getInventory();
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(inventory);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    inventory = (Inventory) in.readObject();
  }

  /* Update Loop */
  @Override
  public void update() {
    super.update();
    updateSlots();
    if(validateContense()) {
      if(energyStorage.getEnergyStored() >= energyConsumption){
        energyStorage.modifyEnergyStored(-energyConsumption);
        tickCount +=1;
        if(tickCount==cookingTime){
          tickCount =0;
          inputSlot.setAmount(inputSlot.getAmount()-1);
          if(outputSlot==null) { //TODO ensure this ItemStack modification effects the items in the inventory
            outputSlot = currentRecipe.getResult();
          } else {
            outputSlot.setAmount(outputSlot.getAmount() + currentRecipe.getResult().getAmount());
          }
          inventory.setItem(OUTPUT_LOCATION, outputSlot);
        }
      }
      runningContainer.setT(true);
    }else {
      runningContainer.setT(false);
    }
    progressContainer.setT(((double) tickCount) / cookingTime);
  }
  private void updateSlots(){
    inputSlot = inventory.getItem(INPUT_LOCATION);
    outputSlot = inventory.getItem(OUTPUT_LOCATION);
  }

  private boolean validateContense() {
    if(inputSlot==null) return false;
    String inputType = inputSlot.getType().toString();
    Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
    while(recipeIterator.hasNext()) {
      Recipe recipe = recipeIterator.next();
      if(!(recipe instanceof FurnaceRecipe)) continue;
      FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;

      if(furnaceRecipe.getInput().getType().toString() != inputType) continue;
      currentRecipe = furnaceRecipe;
      if(outputSlot==null) return true;
      if(outputSlot.getType().toString()==recipe.getResult().getType().toString() && outputSlot.getAmount() < outputSlot.getMaxStackSize()) return true;
    }
    currentRecipe = null;
    return false;
  }
}
