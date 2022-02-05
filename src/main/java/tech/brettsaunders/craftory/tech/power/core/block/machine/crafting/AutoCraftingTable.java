package tech.brettsaunders.craftory.tech.power.core.block.machine.crafting;

import io.papermc.lib.PaperLib;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.inventory.meta.ItemMeta;
import tech.brettsaunders.craftory.Constants;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;

public class AutoCraftingTable extends BaseMachine implements Listener, IHopperInteract {

  private static final int MAX_RECEIVE = 10000;
  private static final int OUTPUT_SLOT = 25;
  protected static final Map<BlockFace, Set<Integer>> inputFaces = new EnumMap<>(BlockFace.class);
  protected static final Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  private static final List<Integer> gridSlots = Arrays.asList(3, 4, 5, 12, 13, 14, 21, 22, 23);
  private static final Set<Integer> inputSlots = new HashSet<>(Arrays.asList(39, 40, 41, 42, 43, 48, 49, 50, 51, 52));

  // Crafting
  private Recipe recipe;
  private Map<RecipeChoice, Integer> ingredients;

  private int energyPerCraft = Constants.Energy.BASE_ENERGY_PER_CRAFT;

  static {
    inputFaces.put(BlockFace.UP, inputSlots);
    inputFaces.put(BlockFace.EAST, inputSlots);
    inputFaces.put(BlockFace.WEST, inputSlots);
    inputFaces.put(BlockFace.NORTH, inputSlots);
    inputFaces.put(BlockFace.SOUTH, inputSlots);
    outputFaces.put(BlockFace.DOWN, OUTPUT_SLOT);
  }

  public AutoCraftingTable(Location location, Player player) {
    super(location, Blocks.AUTO_CRAFTER, (byte) 0,MAX_RECEIVE);
    setup();
    energyStorage = new EnergyStorage(40000);
  }

  public AutoCraftingTable() {
    super();
    setup();
  }

  private void setup() {
    ingredients = new HashMap<>();
    outputLocations = new ArrayList<>();
    outputLocations.add(0, OUTPUT_SLOT);
    inputLocations =  new ArrayList<>(gridSlots);
    List<Integer> intractable = new ArrayList<>(gridSlots);
    intractable.add(OUTPUT_SLOT);
    intractable.addAll(inputSlots);
    interactableSlots = new HashSet<>(intractable);
    Events.registerEvents(this);
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    checkRecipe(new ArrayList<>(), new ItemStack(Material.AIR), true);
  }

  /**
   * Section deals with calculating the recipe
   */

  @EventHandler
  public void inventoryItemMove(InventoryClickEvent e) {
    if (!e.getInventory().equals(inventoryInterface)) return;

    // Disable shift clicking items into crafting slots
    if (e.getClick().isShiftClick() && gridSlots.contains(e.getSlot())) {
      e.setCancelled(true);
      return;
    }

    // Check if recipe changed
    if (gridSlots.contains(e.getRawSlot()) && e.getResult() == Result.ALLOW) {
      checkRecipe(List.of(e.getRawSlot()), e.getCursor(), false);
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

  private void checkRecipe(List<Integer> slots, ItemStack itemStack, boolean startUp) {
    ItemStack[] items = getCraftingGridItems(false);

    if (!startUp) {
      for (Integer slot : slots) {
        // Set itemstacks changed in event
        items[gridSlots.indexOf(slot)] = itemStack;
      }
    }

    recipe = Bukkit.getCraftingRecipe(items, location.getWorld());
    if (recipe == null) {
      inventoryInterface.setItem(7, new ItemStack(Material.AIR));
    } else {
      ItemStack result = recipe.getResult();
      ItemMeta meta = result.getItemMeta();
      energyPerCraft = Constants.Energy.BASE_ENERGY_PER_CRAFT + Constants.Energy.BASE_ENERGY_PER_CRAFT * (int)Math.round(1 + 0.15 * Craftory.complexityManager.getItemTier(result.getType().toString()));
      meta.setLore(List.of("Consumes " + energyPerCraft + " RE per craft"));
      result.setItemMeta(meta);
      inventoryInterface.setItem(7, result);
    }
    updateIngredients();
  }

  private ItemStack[] getCraftingGridItems(boolean clone) {
    ItemStack[] items = new ItemStack[9];
    for (int i = 0; i < gridSlots.size(); i++) {
      ItemStack item = inventoryInterface.getItem(gridSlots.get(i));
      if (item != null) {
        if (clone) {
          items[i] = item.clone();
        } else {
          items[i] = item;
        }
      } else {
        items[i] = null;
      }
    }
    return items;
  }

  private void updateIngredients(){
    // Collect needed ingredients from recipe
    ingredients.clear();
    if (recipe instanceof ShapelessRecipe shapelessRecipe) {
      for (RecipeChoice choice : shapelessRecipe.getChoiceList()) {
        if (choice != null)
          ingredients.put(choice, ingredients.getOrDefault(choice, 0) + 1);
      }
    } else if (recipe instanceof ShapedRecipe shapedRecipe) {
      for (String row : shapedRecipe.getShape()) {
        for (char c : row.toCharArray()) {
          RecipeChoice choice = shapedRecipe.getChoiceMap().get(c);
          if (choice != null)
            ingredients.put(choice, ingredients.getOrDefault(choice, 0) + 1);
        }
      }
    }
  }

  /**
   * Section deals with the GUI
   */

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.AUTOCRAFTER.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    this.inventoryInterface = inventory;
  }

  /**
   * Section deals with crafting the item from ingredients
   */

  @Ticking(ticks = 20)
  public void autoCraft() {
    if (recipe != null) {
      boolean hasEnergy = energyStorage.getEnergyStored() >= energyPerCraft;
      final ItemStack outputItem = inventoryInterface.getItem(OUTPUT_SLOT);
      boolean hasResultSpace = true;
      if (outputItem != null && outputItem.getType() != Material.AIR) {
        hasResultSpace = (outputItem.getAmount() + recipe.getResult().getAmount() <= outputItem.getMaxStackSize())
          && outputItem.getType() == recipe.getResult().getType();
      };

      if (hasEnergy && hasResultSpace && hasIngredients()) {
          runningContainer.setT(true);
        if (outputItem == null || outputItem.getType() == Material.AIR) {
            inventoryInterface.setItem(OUTPUT_SLOT, recipe.getResult());
          } else if (outputItem.getType() == recipe.getResult().getType() && outputItem.getAmount() < outputItem.getMaxStackSize()) {
            outputItem.setAmount(Math.min(outputItem.getAmount() + recipe.getResult().getAmount(), outputItem.getMaxStackSize()));
          }
          // Consume ingredients
          for (Map.Entry<RecipeChoice, Integer> entry : ingredients.entrySet()) {
            int amount = entry.getValue();
            for (Integer slot : inputSlots) {
              ItemStack stack = inventoryInterface.getItem(slot);
              if (stack != null && entry.getKey().test(stack)) {
                int reduceAmount = Math.min(stack.getAmount(), amount);
                amount -= reduceAmount;
                stack.setAmount(stack.getAmount() - reduceAmount);
                inventoryInterface.setItem(slot, stack);
              }
              if (amount <= 0) {
                break;
              }
            }

          }
          energyStorage.modifyEnergyStored(-energyPerCraft);
      } else {
        runningContainer.setT(false);
      }
    } else {
      runningContainer.setT(false);
    }
  }

  private boolean hasIngredients() {
    if (ingredients.size() == 0) return false;

    for (Map.Entry<RecipeChoice, Integer> entry : ingredients.entrySet()) {
      int amount = 0;
      for (Integer slot : inputSlots) {
        ItemStack slotItem = inventoryInterface.getItem(slot);
        if (slotItem != null && entry.getKey().test(slotItem)) {
          amount += slotItem.getAmount();
        }
        if (amount >= entry.getValue()) {
          break;
        }
      }
      if (amount < entry.getValue()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Section deals with item transport
   */

  @Override
  public void processHoppers() {
    if (inventoryInterface == null) {
      return;
    }
    Map<BlockFace, Set<Integer>> inputFaces = ((IHopperInteract) this).getInputFaces();

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

                  if (slotStack == null || slotStack.getType() == Material.AIR) {
                    for (ItemStack hopperItem : hopperItems) {
                      if (hopperItem == null) continue;
                      inventoryInterface.setItem(slot, hopperItem);
                      inventoryInterface.getItem(slot).setAmount(1);
                      hopperItem.setAmount(hopperItem.getAmount() - 1);
                      break outerloop;
                    }
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
    if (cachedSidesConfig.containsKey(BlockFace.DOWN)
            && cachedSidesConfig.get(BlockFace.DOWN).equals(INTERACTABLEBLOCK.HOPPER_OUT)) {
      ItemStack stack = inventoryInterface.getItem(((IHopperInteract) this).getOutputSlot());
      if (stack != null) {
        ItemStack toMove = stack.clone();
        toMove.setAmount(1);
        Inventory hopperInventory =
                ((Hopper) location.getBlock().getRelative(BlockFace.DOWN).getState()).getInventory();
        HashMap<Integer, ItemStack> failedItems = hopperInventory.addItem(toMove);
        if (failedItems.isEmpty()) {
          stack.setAmount(stack.getAmount() - 1);
          inventoryInterface.setItem(((IHopperInteract) this).getOutputSlot(), stack);
        }
      }
    }
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
  public Integer getOutputSlot() {
    return OUTPUT_SLOT;
  }
}
