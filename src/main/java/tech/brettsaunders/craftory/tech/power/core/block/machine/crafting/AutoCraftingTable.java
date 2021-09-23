package tech.brettsaunders.craftory.tech.power.core.block.machine.crafting;

import io.papermc.lib.PaperLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import tech.brettsaunders.craftory.utils.recipes.RecipeUtils;

public class AutoCraftingTable extends BaseMachine implements Listener, IHopperInteract {

  private static final int MAX_RECEIVE = 10000;
  private static final int ENERGY_PER_ACTION = 250;
  private static final int OUTPUT_SLOT = 34;
  protected static final Map<BlockFace, Set<Integer>> inputFaces = new EnumMap<>(BlockFace.class);
  protected static final Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  private static final List<Integer> gridSlots = Arrays.asList(12, 13, 14, 21, 22, 23, 30, 31, 32);
  private ItemStack result;

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
    setup();
    Events.registerEvents(this);
    energyStorage = new EnergyStorage(40000);
  }

  public AutoCraftingTable() {
    super();
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
    ArrayList<ItemStack> ingredients = new ArrayList<>();
    for (int i = 0; i < gridSlots.size(); i++) {
      ingredients.add(i, inventoryInterface.getItem(gridSlots.get(i)));
    }
    if (!startUp) {
      for (Integer slot : slots) {
        ingredients.set(gridSlots.indexOf(slot), itemStack);
      }
    }

    Optional<ItemStack> result = RecipeUtils.getRecipeTree().find(ingredients);
    if (result.isPresent()) {
      this.result = result.get();
    } else {
      this.result = null;
    }
  }

  @Ticking(ticks = 20)
  public void autoCraft() {
    if (result != null) {
      boolean hasIngredients =
          gridSlots.stream()
                   .map(slot -> inventoryInterface.getItem(slot))
                   .filter(item -> item != null)
                   .allMatch(itemStack -> itemStack.getAmount() > 1);
      boolean hasEnergy = energyStorage.getEnergyStored() >= ENERGY_PER_ACTION;
      final ItemStack outputItem = inventoryInterface.getItem(OUTPUT_SLOT);
      boolean hasResultSpace = (outputItem == null || outputItem.getAmount() + result.getAmount() <= outputItem.getMaxStackSize())
          && outputItem.getType() == result.getType();

      if (hasEnergy && hasIngredients && hasResultSpace) {
        runningContainer.setT(true);
        final ItemStack item = outputItem;
        if (item == null || item.getType() == Material.AIR) {
          inventoryInterface.setItem(OUTPUT_SLOT, result);
        } else if (item.getType() == result.getType() && item.getAmount() < item.getMaxStackSize()) {
          item.setAmount(Math.min(item.getAmount() + result.getAmount(), item.getMaxStackSize()));
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
