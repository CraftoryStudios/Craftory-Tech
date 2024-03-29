/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators;

import io.github.bakedlibs.dough.protection.Interaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;

public class BlockBreaker extends BaseMachine {

  private static final byte C_LEVEL = 0;
  private static final int MAX_RECEIVE = 10000;
  private static final int SLOT = 22;

  private static final int ENERGY_REQUIRED = 1200;
  private Location breakLoc;
  private Location opposite;
  private int lastRedstoneStrength = 0;

  private Optional<Inventory> outputInventory;

  @Persistent
  protected UUID owner;

  public BlockBreaker(Location location, Player p) {
    super(location, Blocks.BLOCK_BREAKER, C_LEVEL, MAX_RECEIVE);
    setup();
    energyStorage = new EnergyStorage(40000);
    outputInventory = Optional.empty();
    owner = p.getUniqueId();
  }

  public BlockBreaker() {
    super();
    setup();
    outputInventory = Optional.empty();
  }

  @Override
  protected void processComplete() {
    //No Implementation
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    breakLoc = location.getBlock().getRelative(direction).getLocation();
    opposite = location.getBlock().getRelative(direction.getOppositeFace()).getLocation();
    setOutputInventory(opposite.getBlock());
  }

  private void setup() {
    outputLocations = new ArrayList<>();
    outputLocations.add(0, SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }


  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.BLANK.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage, 13));
    this.inventoryInterface = inventory;
  }

  @EventHandler
  public void onChestPlace(BlockPlaceEvent e) {
    final Block blockPlaced = e.getBlockPlaced();
    if (!blockPlaced.getLocation().equals(opposite)) {
      return;
    }
    setOutputInventory(blockPlaced);
  }

  private void setOutputInventory(Block block) {
    if (block.getState() instanceof InventoryHolder) {
      InventoryHolder ih = (InventoryHolder) block.getState();
      outputInventory = Optional.of(ih.getInventory());
    }
  }

  @EventHandler
  public void onChestRemove(BlockBreakEvent e) {
    final Block block = e.getBlock();
    if (!block.getLocation().equals(opposite)) {
      return;
    }
    if (outputInventory.isPresent()) {
      outputInventory = Optional.empty();
    }
  }

  @EventHandler
  public void onRedstonePower(BlockPhysicsEvent e) {
    if (!e.getBlock().getLocation().equals(location)) {
      return;
    }
    OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
    if (lastRedstoneStrength != 0) {
      lastRedstoneStrength = e.getBlock().getBlockPower();
      return;
    } else if (e.getBlock().getBlockPower() > 0 && checkPowerRequirement()) {
      if (breakLoc.getBlock().isEmpty()) {
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED / 10);
      } else if (Craftory.protectionManager.hasPermission(player, breakLoc.getBlock(), Interaction.BREAK_BLOCK)){
        Block block = breakLoc.getBlock();
        if (Craftory.customBlockManager.isCustomBlock(breakLoc)) {
          Optional<ItemStack> itemStack = Craftory.customBlockManager.breakCustomBlock(breakLoc);
          itemStack.ifPresent(this::dropItem);
        } else {
          block.getDrops().forEach(this::dropItem);
          block.setType(Material.AIR);
        }
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED);
      }
    }
    lastRedstoneStrength = e.getBlock().getBlockPower();
  }

  private void dropItem(ItemStack itemStack) {
    if (outputInventory.isPresent()) {
      HashMap<Integer, ItemStack> result = outputInventory.get().addItem(itemStack);
      if (result.size() > 0) {
        result.forEach((i, item) -> location.getWorld().dropItemNaturally(opposite, item));
      }
    } else {
      location.getWorld().dropItem(opposite, itemStack);
    }
  }

  private boolean checkPowerRequirement() {
    return energyStorage.getEnergyStored() > ENERGY_REQUIRED;
  }


  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {
    //No Implementation
  }

}
