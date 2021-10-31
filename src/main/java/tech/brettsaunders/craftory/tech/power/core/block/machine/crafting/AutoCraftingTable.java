package tech.brettsaunders.craftory.tech.power.core.block.machine.crafting;

import io.papermc.lib.PaperLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

public class AutoCraftingTable extends BaseMachine implements Listener, IHopperInteract {

  private static final int MAX_RECEIVE = 10000;
  private static final int ENERGY_PER_ACTION = 250;
  private static final int OUTPUT_SLOT = 34;
  protected static final Map<BlockFace, Set<Integer>> inputFaces = new EnumMap<>(BlockFace.class);
  protected static final Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  private static final List<Integer> gridSlots = Arrays.asList(12, 13, 14, 21, 22, 23, 30, 31, 32);

  // Crafting
  private Recipe recipe;
  private final List<RecipeChoice> necessaryIngredients;

  static {
    Set<Integer> gridSet = new HashSet<>(gridSlots);
    inputFaces.put(BlockFace.UP, gridSet);
    inputFaces.put(BlockFace.EAST, gridSet);
    inputFaces.put(BlockFace.WEST, gridSet);
    inputFaces.put(BlockFace.NORTH, gridSet);
    inputFaces.put(BlockFace.SOUTH, gridSet);
    outputFaces.put(BlockFace.DOWN, OUTPUT_SLOT);
  }

  public AutoCraftingTable(Location location, Player player) {
    super(location, Blocks.AUTO_CRAFTER, (byte) 0,MAX_RECEIVE);
    necessaryIngredients = new ArrayList<>();
    setup();
    Events.registerEvents(this);
    energyStorage = new EnergyStorage(40000);
  }

  public AutoCraftingTable() {
    super();
    necessaryIngredients = new ArrayList<>();
    setup();
    checkRecipe(new ArrayList<>(), new ItemStack(Material.AIR), true);
  }

  private void setup() {
    outputLocations = new ArrayList<>();
    outputLocations.add(0, OUTPUT_SLOT);
    inputLocations =  new ArrayList<>(gridSlots);
    List<Integer> intractable = new ArrayList<>(gridSlots);
    intractable.add(OUTPUT_SLOT);
    interactableSlots = new HashSet<>(intractable);
  }

  @EventHandler
  public void inventoryItemMove(InventoryClickEvent e) {
    if (!e.getInventory().equals(inventoryInterface)) return;

    // Check if recipe changed
    if (gridSlots.contains(e.getRawSlot()) && e.getResult() == Result.ALLOW) {
      checkRecipe(Arrays.asList(e.getRawSlot()), e.getCursor(), false);
    }
  }

  @EventHandler
  public void inventoryItemDrag(InventoryDragEvent e) {
    if (!e.getInventory().equals(inventoryInterface)) return;

    List<Integer> commonSlots = gridSlots.stream()
        .filter(e.getInventorySlots()::contains)
        .collect(Collectors.toList());

    // Check if recipe changed
    if (commonSlots.size() > 0 && e.getResult() != Result.DENY) {
      checkRecipe(commonSlots, e.getOldCursor(), false);
    }
  }



  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.AUTOCRAFTER.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 7));
    this.inventoryInterface = inventory;
  }

  public void checkRecipe(List<Integer> slots, ItemStack itemStack, boolean startUp) {
    ItemStack[] items = getGridItems(false);

    if (!startUp) {
      for (Integer slot : slots) {
        // Set itemstacks changed in event
        items[gridSlots.indexOf(slot)] = itemStack;
      }
    }

    recipe = Bukkit.getCraftingRecipe(items, location.getWorld());
    updateIngredients();
  }

  private ItemStack[] getGridItems(boolean clone) {
    ItemStack[] items = new ItemStack[9];
    for (int i = 0; i < gridSlots.size(); i++) {
      if (inventoryInterface.getItem(gridSlots.get(i)) != null) {
        if (clone) {
          items[i] = inventoryInterface.getItem(gridSlots.get(i)).clone();
        } else {
          items[i] = inventoryInterface.getItem(gridSlots.get(i));
        }
      } else {
        items[i] = null;
      }
    }
    return items;
  }

  private  void updateIngredients(){
    // Collect needed ingredients from recipe
    necessaryIngredients.clear();
    if (recipe instanceof ShapelessRecipe shapelessRecipe) {
      necessaryIngredients.addAll(shapelessRecipe.getChoiceList());
    } else if (recipe instanceof ShapedRecipe shapedRecipe) {
      Map<Character, Integer> charCounts = new HashMap<>();
      for (String row : shapedRecipe.getShape()) {
        for (char c : row.toCharArray()) {
          charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }
      }
      for (Character c : charCounts.keySet()) {
        RecipeChoice ingredientChoice = shapedRecipe.getChoiceMap().get(c);
        if (ingredientChoice != null) {
          necessaryIngredients.addAll(Collections.nCopies(charCounts.get(c), ingredientChoice));
        }
      }
    }
  }

  @Ticking(ticks = 20)
  public void autoCraft() {
    if (recipe != null) {
      boolean hasEnergy = energyStorage.getEnergyStored() >= ENERGY_PER_ACTION;
      final ItemStack outputItem = inventoryInterface.getItem(OUTPUT_SLOT);
      boolean hasResultSpace = (outputItem == null || outputItem.getAmount() + recipe.getResult().getAmount() <= outputItem.getMaxStackSize())
          && outputItem.getType() == recipe.getResult().getType();

      if (hasEnergy && hasResultSpace && hasIngredients()) {
          runningContainer.setT(true);
          final ItemStack item = outputItem;
          if (item == null || item.getType() == Material.AIR) {
            inventoryInterface.setItem(OUTPUT_SLOT, recipe.getResult());
          } else if (item.getType() == recipe.getResult().getType() && item.getAmount() < item.getMaxStackSize()) {
            item.setAmount(Math.min(item.getAmount() + recipe.getResult().getAmount(), item.getMaxStackSize()));
          }
          for (Integer slot : gridSlots) {
            ItemStack stack = inventoryInterface.getItem(slot);
            if (stack != null) {
              stack.setAmount(stack.getAmount() - 1);
              inventoryInterface.setItem(slot, stack);
            }
          }
          energyStorage.modifyEnergyStored(-ENERGY_PER_ACTION);
      } else {
        runningContainer.setT(false);
      }
    } else {
      runningContainer.setT(false);
    }
  }

  private boolean hasIngredients() {
    if (necessaryIngredients.size() == 0) return false;

    if (recipe instanceof ShapedRecipe) {
      ItemStack[] items = getGridItems(false);
      for (int i = 0; i < necessaryIngredients.size(); i++) {
        if (!necessaryIngredients.get(i).test(items[i])) {
          return false;
        }
      }
      return true;
    } else {
      List<ItemStack> items = Arrays.asList(getGridItems(true));
      int neededIngredientsCount = necessaryIngredients.size();
      for (RecipeChoice neededIngredient : necessaryIngredients) {
        for (int i = 0; i < items.size(); i++) {
          if (neededIngredient.test(items.get(i))) {
            if (items.get(i).getAmount() > 1) {
              items.get(i).setAmount(items.get(i).getAmount() - 1);
            } else {
              items.remove(i);
            }
            neededIngredientsCount--;
            break;
          }
        }
      }
      return neededIngredientsCount == 0;
    }
  }

  @Override
  public void processHoppers() {
    if (!(this instanceof IHopperInteract)) {
      return;
    }
    if (inventoryInterface == null) {
      return;
    }
    Map<BlockFace, Set<Integer>> inputFaces = ((IHopperInteract) this).getInputFaces();
    Map<BlockFace, Integer> outputFaces = ((IHopperInteract) this).getOutputFaces();

    // Hopper Input
    inputFaces.forEach(
        (face, inputSlots) -> {
          if (cachedSidesConfig.containsKey(face)
              && cachedSidesConfig.get(face).equals(INTERACTABLEBLOCK.HOPPER_IN)) {

            final Block relative = location.getBlock().getRelative(face);
            if (!relative.isBlockPowered()) {
              BlockState relativeBlockState = PaperLib.getBlockState(relative, false).getState();
              if (relativeBlockState instanceof Hopper) {
                ItemStack[] hopperItems = ((Hopper) relative.getState()).getInventory().getContents();
                ItemStack slotStack = null;
                int currentSlot = -1;

                // Loop inventory slots that accept items
                outerloop:
                for (Integer slot : inputSlots) {
                  currentSlot = slot;
                  slotStack = inventoryInterface.getItem(slot);

                  // ADDED fro auto crafting
                  if (slotStack == null || slotStack.getType() == Material.AIR) {
                    continue;
                  }

                  String slotItemType = slotStack.getType().toString();
                  boolean hasStackSpace = slotStack.getAmount() < slotStack.getMaxStackSize();
                  // Loop hopper items
                  for (ItemStack hopperItem : hopperItems) {
                    if (hopperItem == null) continue;
                    if (slotItemType.equals(hopperItem.getType().toString()) && hasStackSpace){
                      slotStack.setAmount(slotStack.getAmount() + 1);
                      hopperItem.setAmount(hopperItem.getAmount() - 1);
                      break outerloop;
                    }
                  }

                }
                if (slotStack != null && currentSlot != -1)
                  inventoryInterface.setItem(currentSlot, slotStack);
              } else {
                cachedSidesConfig.replace(face, INTERACTABLEBLOCK.NONE);
              }
            }
          }
        });

    // Hopper Output
    outputFaces.forEach(
        (face, slot) -> {
          if (cachedSidesConfig.containsKey(face)
              && cachedSidesConfig.get(face).equals(INTERACTABLEBLOCK.HOPPER_OUT)) {
            ItemStack stack = inventoryInterface.getItem(slot);
            if (stack != null) {
              ItemStack toMove = stack.clone();
              toMove.setAmount(1);
              Inventory hopperInventory =
                  ((Hopper) location.getBlock().getRelative(face).getState()).getInventory();
              HashMap<Integer, ItemStack> failedItems = hopperInventory.addItem(toMove);
              if (failedItems.isEmpty()) {
                stack.setAmount(stack.getAmount() - 1);
                inventoryInterface.setItem(slot, stack);
              }
            }
          }
        });
  }

  @Override
  public void updateMachine() {
  }

  @Override
  public void soundLoop() {

  }

  @Override
  public void  refreshLight() {

  }

  @Override
  protected void processComplete() {

  }
  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {

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
