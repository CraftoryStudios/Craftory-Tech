package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.utils.HopperItemMovement;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseElectricFurnace extends BaseMachine implements Externalizable {

  /* Static Constants Protected */
  protected static final int[] COOKING_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final long serialVersionUID = 10005L;
  private static final int INPUT_LOCATION = 22;
  private static final int OUTPUT_LOCATION = 26;
  /* Per Object Variables Saved */
  private ItemStack inputSlot;
  private ItemStack outputSlot;

  /* Per Object Variables Not-Saved */
  private transient Inventory inventoryInterface;
  private transient int cookingTime;
  private transient int energyConsumption;
  private transient int tickCount = 0;
  private transient FurnaceRecipe currentRecipe = null;
  private transient VariableContainer<Boolean> runningContainer;
  private transient VariableContainer<Double> progressContainer;
  private transient int HOPPER_DELAY = 8;
  private transient int hopperInCounter = 0;
  private transient int hopperOutCounter = 0;

  /* Construction */
  public BaseElectricFurnace(Location location, byte level) {
    super(location, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    if (ItemsAdder.areItemsLoaded()) {
      setupGUI();
    }
  }

  /* Saving, Setup and Loading */
  public BaseElectricFurnace() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    cookingTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION, OUTPUT_LOCATION));
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    updateSlots();
    out.writeObject(inputSlot);
    out.writeObject(outputSlot);
    out.writeObject(energyStorage);

  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    inputSlot = (ItemStack) in.readObject();
    outputSlot = (ItemStack) in.readObject();
    energyStorage = (EnergyStorage) in.readObject();
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Electric Furnace", new FontImageWrapper("extra:cell"));
    addGUIComponent(
        new GOneToOneMachine(inventory, 24, progressContainer, INPUT_LOCATION, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer));
    inventory.setItem(INPUT_LOCATION, inputSlot);
    inventory.setItem(OUTPUT_LOCATION, outputSlot);
    this.inventoryInterface = inventory;
    HashSet<Integer> protectedslots = new HashSet<>();
    protectedslots.add(INPUT_LOCATION);
    protectedslots.add(OUTPUT_LOCATION);
    fillBlankSlots(protectedslots);
  }

  /* Update Loop */
  @Override
  public void update() {
    super.update();
    if (inventoryInterface == null) {
      return;
    }
    updateSlots();
    if (validateContense() && energyStorage.getEnergyStored() >= energyConsumption) {
      energyStorage.modifyEnergyStored(-energyConsumption);
      tickCount += 1;
      if (tickCount == cookingTime) {
        tickCount = 0;
        inputSlot.setAmount(inputSlot.getAmount() - 1);
        if (outputSlot == null) {
          outputSlot = currentRecipe.getResult();
        } else {
          outputSlot.setAmount(outputSlot.getAmount() + currentRecipe.getResult().getAmount());
        }
        inventoryInterface.setItem(OUTPUT_LOCATION, outputSlot);
      }
      runningContainer.setT(true);
    } else {
      runningContainer.setT(false);
    }
    progressContainer.setT(((double) tickCount) / cookingTime);
    processHoppers();
  }

  /* Internal Helper Functions */
  private void updateSlots() {
    inputSlot = inventoryInterface.getItem(INPUT_LOCATION);
    outputSlot = inventoryInterface.getItem(OUTPUT_LOCATION);
  }

  private static final BlockFace[] inputDirections = {BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP};
  private void processHoppers() {
    //Process incoming hoppers
    if(hopperInCounter != 0) hopperInCounter-=1;
    else {
      ItemStack stack = HopperItemMovement.moveItemsIn(location,inputSlot);
      if(stack !=null) {
        inputSlot = stack;
        hopperInCounter = HOPPER_DELAY;
      }
    }
    if(hopperOutCounter != 0) hopperOutCounter-=1;
    else if(HopperItemMovement.moveItemsOut(location, outputSlot)) hopperOutCounter = HOPPER_DELAY;
    /*
    Block b;
    ItemStack[] hopperItems;
    BlockFace facing;
    if(hopperInCounter==0 && (inputSlot==null || inputSlot.getAmount() < inputSlot.getMaxStackSize())){
      for(BlockFace face: inputDirections) {
        b = location.getBlock().getRelative(face);
        if (b.getType().equals(Material.HOPPER)){
          facing = ((Directional) b.getBlockData()).getFacing();
          if(!facing.equals(face.getOppositeFace())) continue; //Skip if hopper is not facing block
          hopperItems = ((Hopper) b.getState()).getInventory().getContents();
          for(ItemStack item: hopperItems){
            if(item==null) continue;
            if(inputSlot==null){
              inputSlot = item.clone();
              inputSlot.setAmount(1);
              item.setAmount(item.getAmount()-1);
              hopperInCounter = HOPPER_DELAY;
              break;
            } else if(inputSlot.getType().toString().equals(item.getType().toString()) && inputSlot.getAmount() < inputSlot.getMaxStackSize()) {
              inputSlot.setAmount(inputSlot.getAmount()+1);
              item.setAmount(item.getAmount()-1);
              hopperInCounter = HOPPER_DELAY;
              break;
            }
          }
        }
      }
    }
    //Process outgoing hopper
    if(hopperOutCounter==0 && outputSlot!=null){
      //Only do if there is something to output
      b = location.getBlock().getRelative(BlockFace.DOWN);
      if (b.getType().equals(Material.HOPPER)){
        ItemStack toMove = outputSlot.clone();
        toMove.setAmount(1);
        Inventory hopperInventory = ((Hopper)b.getState()).getInventory();
        HashMap<Integer, ItemStack> failedItems = hopperInventory.addItem(toMove);
        if(failedItems.isEmpty()){
          outputSlot.setAmount(outputSlot.getAmount()-1);
        } else {
          hopperOutCounter = HOPPER_DELAY;
        }
      }
    } */
    //Set inventory to equal slots
    inventoryInterface.setItem(INPUT_LOCATION, inputSlot);
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlot);
  }

  private boolean validateContense() {
    if (inputSlot == null) {
      return false;
    }
    String inputType = inputSlot.getType().toString();
    //If the recipe is unchanged there is no need to find the recipe.
    if (currentRecipe != null && currentRecipe.getInput().getType().toString().equals(inputType)) {
      if (outputSlot == null) {
        return true;
      }
      if (outputSlot.getType().toString().equals(currentRecipe.getResult().getType().toString())
          && outputSlot.getAmount() < outputSlot.getMaxStackSize()) {
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
      if (outputSlot == null) {
        return true;
      }
      if (outputSlot.getType().toString().equals(recipe.getResult().getType().toString())
          && outputSlot.getAmount() < outputSlot.getMaxStackSize()) {
        return true;
      }
    }
    currentRecipe = null;
    return false;
  }

}
