/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.manipulators;

import com.gmail.nossr50.mcMMO;
import io.github.bakedlibs.dough.protection.Interaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.gui_components.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.Log;

public class BlockPlacer extends BaseMachine implements IHopperInteract {

  private static final byte C_LEVEL = 0;
  private static final int MAX_RECEIVE = 10000;
  private static final int SLOT = 22;
  protected static final Map<BlockFace, Set<Integer>> inputFaces = new EnumMap<>(BlockFace.class);
  protected static final Map<BlockFace, Integer> outputFaces = new EnumMap<>(BlockFace.class);
  private static final int ENERGY_REQUIRED = 1000;
  private Location placeLoc;
  private int lastRedstoneStrength = 0;

  @Persistent
  protected UUID owner;

  static {
    inputFaces.put(BlockFace.NORTH, Collections.singleton(SLOT));
    inputFaces.put(BlockFace.EAST, Collections.singleton(SLOT));
    inputFaces.put(BlockFace.SOUTH, Collections.singleton(SLOT));
    inputFaces.put(BlockFace.WEST, Collections.singleton(SLOT));
    inputFaces.put(BlockFace.UP, Collections.singleton(SLOT));

    outputFaces.put(BlockFace.DOWN, SLOT);
  }

  public BlockPlacer(Location location, Player p) {
    super(location, Blocks.BLOCK_PLACER, C_LEVEL, MAX_RECEIVE);
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
    setup();
    energyStorage = new EnergyStorage(40000);
    owner = p.getUniqueId();
  }

  public BlockPlacer() {
    super();
    setup();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    placeLoc = location.getBlock().getRelative(direction).getLocation();
  }

  private void setup() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0, SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
  }

  @Override
  public void updateMachine() {
    //No Implementation
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.PLACER.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    this.inventoryInterface = inventory;
  }

  @EventHandler
  public void onRedstonePower(BlockPhysicsEvent e) {
    if (!e.getBlock().getLocation().equals(location)) {
      return;
    }
    if (placeLoc.getBlock().getType() != Material.AIR) {
      return;
    }
    OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
    if (lastRedstoneStrength != 0) {
      lastRedstoneStrength = e.getBlock().getBlockPower();
      return;
    } else if (e.getBlock().getBlockPower() > 0 && checkPowerRequirement()
        && inventoryInterface != null
        && Craftory.protectionManager.hasPermission(player, placeLoc.getBlock(), Interaction.PLACE_BLOCK)) {

      final ItemStack item = inventoryInterface.getItem(SLOT);
      if (item == null) {
        lastRedstoneStrength = e.getBlock().getBlockPower();
        return;
      }
      if (item.getType() == Material.AIR || !item.getType().isBlock()) {
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED / 10);
      } else {
        if (CustomItemManager.isCustomItem(item, true)) {
          //No Custom Block Placing
        } else {
          placeLoc.getBlock().setType(item.getType());
          if (Craftory.plugin.isPluginLoaded("mcMMO")) {
            mcMMO.getPlaceStore().setTrue(placeLoc.getBlock());
            Log.debug("Block placed by placer marked as placed by a player");
          }
        }
        item.setAmount(item.getAmount() - 1);
        energyStorage.modifyEnergyStored(-ENERGY_REQUIRED);
      }
    }
    lastRedstoneStrength = e.getBlock().getBlockPower();
  }

  private boolean checkPowerRequirement() {
    return energyStorage.getEnergyStored() > ENERGY_REQUIRED;
  }

  @Override
  protected void processComplete() {
    //No Implementation
  }

  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {
    //No Implementation
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
