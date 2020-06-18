package tech.brettsaunders.craftory.tech.power.core.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;
import tech.brettsaunders.craftory.tech.power.core.block.cell.DiamondCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.EmeraldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.GoldCell;
import tech.brettsaunders.craftory.tech.power.core.block.cell.IronCell;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.DiamondElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.EmeraldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.GoldElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.IronElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.foundry.IronFoundry;
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelGenerator;
import tech.brettsaunders.craftory.tech.power.core.utils.PoweredBlockType;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredBlockManager implements Listener, ITickable {

  public static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  private static final String DATA_PATH;

  static {
    DATA_PATH = Utilities.DATA_FOLDER + File.separator + "poweredBlock.data";
  }

  private final HashMap<Location, PowerGridManager> powerGrids;
  public HashSet<PowerGridManager> powerGridManagers;
  private HashMap<Location, PoweredBlock> poweredBlocks;
  private HashMap<World, HashSet> loadedChunkWorlds;
  private HashMap<UUID, ArrayList<Boolean>> sidesConfigCopying;

  public PoweredBlockManager() {
    poweredBlocks = new HashMap<>();
    powerGridManagers = new HashSet<>();
    powerGrids = new HashMap<>();
    sidesConfigCopying = new HashMap<>();
    loadedChunkWorlds = new HashMap<>();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
    Craftory.tickableBaseManager.addUpdate(this);
  }

  public void onEnable() {
    load();
    poweredBlocks.forEach(((location, poweredBlock) -> poweredBlock.setupGUI()));
  }

  public void onDisable() {
    save();
  }

  public void addPoweredBlock(Location location, PoweredBlock blockPowered) {
    poweredBlocks.put(location, blockPowered);
  }

  public PoweredBlock getPoweredBlock(Location location) {
    return poweredBlocks.get(location);
  }

  public boolean isPoweredBlock(Location location) {
    return poweredBlocks.containsKey(location);
  }

  public void removePoweredBlock(Location location) {
    poweredBlocks.remove(location);
  }

  public void load() {
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(DATA_PATH)));
      PowerBlockManagerData data = (PowerBlockManagerData) in.readObject();
      poweredBlocks = data.poweredBlocks;
      powerGridManagers = data.powerGridManagers;
      in.close();
      Logger.info("Powered Block Data Loaded");
    } catch (FileNotFoundException e) {
      Logger.debug("First Run - Generating Powered Block Data");
    } catch (IOException e) {
      Logger.error("Powered Block Data IO Loading Issue");
      Logger.captureError(e);
    } catch (ClassNotFoundException e) {
      Logger.captureError(e);
    }
  }

  public void save() {
    try {
      PowerBlockManagerData data = new PowerBlockManagerData(poweredBlocks, powerGridManagers);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(DATA_PATH)));
      out.writeObject(data);
      out.close();
      Logger.debug("Powered Block Data Saved");
    } catch (IOException e) {
      Logger.warn("Couldn't save Powered Block Data");
      Logger.captureError(e);
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
    save();
  }

  @EventHandler
  public void onGUIBlockClick(CustomBlockInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if (e.getPlayer().isSneaking() || CustomItemManager.matchCustomItemName(e.getItem(), CoreHolder.Items.CONFIGURATOR)) {
      return;
    }

    if (poweredBlocks.containsKey(e.getBlockClicked().getLocation())) {
      //Open GUI of Powered Block
      poweredBlocks.get(e.getBlockClicked().getLocation()).openGUI(e.getPlayer());
      e.setCancelled(true);
    }
  }

  public boolean isProvider(Location location) {
    if (isPoweredBlock(location)) {
      return poweredBlocks.get(location).isProvider();
    }
    return false;

  }

  public boolean isReceiver(Location location) {
    if (isPoweredBlock(location)) {
      return poweredBlocks.get(location).isReceiver();
    }
    return false;
  }

  /* Block Type Getters */
  public boolean isCell(Location location) {
    if (isPoweredBlock(location)) {
      return poweredBlocks.get(location).isProvider() && poweredBlocks.get(location).isReceiver();
    }
    return false;
  }

  public boolean isGenerator(Location location) {
    if (isPoweredBlock(location)) {
      return poweredBlocks.get(location).isProvider() && !poweredBlocks.get(location).isReceiver();
    }
    return false;
  }

  public boolean isMachine(Location location) {
    if (isPoweredBlock(location)) {
      return !poweredBlocks.get(location).isProvider() && !poweredBlocks.get(location).isReceiver();
    }
    return false;
  }

  /* Events */
  @EventHandler
  public void onPoweredBlockPlace(CustomBlockPlaceEvent event) {
          PoweredBlock poweredBlock = null;
          PoweredBlockType type;

          switch (event.getCustomBlockName()) {
            case CoreHolder.Blocks.IRON_CELL:
              poweredBlock = new IronCell(event.getLocation());
              type = PoweredBlockType.CELL;
              break;
            case CoreHolder.Blocks.GOLD_CELL:
              poweredBlock = new GoldCell(event.getLocation());
              type = PoweredBlockType.CELL;
              break;
            case CoreHolder.Blocks.DIAMOND_CELL:
              poweredBlock = new DiamondCell(event.getLocation());
              type = PoweredBlockType.CELL;
              break;
            case CoreHolder.Blocks.EMERALD_CELL:
              poweredBlock = new EmeraldCell(event.getLocation());
              type = PoweredBlockType.CELL;
              break;

            case CoreHolder.Blocks.SOLID_FUEL_GENERATOR:
              poweredBlock = new SolidFuelGenerator(event.getLocation());
              type = PoweredBlockType.GENERATOR;
              break;
            case CoreHolder.Blocks.POWER_CONNECTOR:
              PowerGridManager manager = new PowerGridManager(event.getLocation());
              getAdjacentPowerBlocks(event.getLocation(), manager);
              addPowerGridManager(event.getLocation(), manager);
              type = PoweredBlockType.CELL;
              break;
            case CoreHolder.Blocks.IRON_ELECTRIC_FURNACE:
              poweredBlock = new IronElectricFurnace(event.getLocation());
              type = PoweredBlockType.MACHINE;
              break;
            case CoreHolder.Blocks.GOLD_ELECTRIC_FURNACE:
              poweredBlock = new GoldElectricFurnace(event.getLocation());
              type = PoweredBlockType.MACHINE;
              break;
            case CoreHolder.Blocks.EMERALD_ELECTRIC_FURNACE:
              poweredBlock = new EmeraldElectricFurnace(event.getLocation());
              type = PoweredBlockType.MACHINE;
              break;
            case CoreHolder.Blocks.DIAMOND_ELECTRIC_FURNACE:
              poweredBlock = new DiamondElectricFurnace(event.getLocation());
              type = PoweredBlockType.MACHINE;
              break;
            case CoreHolder.Blocks.IRON_FOUNDRY:
              poweredBlock = new IronFoundry(event.getLocation());
              type = PoweredBlockType.MACHINE;
              break;
            default:
              return;
          }

          //Carry out PoweredBlock Base Setup
          if (poweredBlock != null) {
            addPoweredBlock(event.getLocation(), poweredBlock);
            if (poweredBlock.isReceiver()) {
              updateAdjacentProviders(event.getLocation(), true, type);
            }
          }
  }

  @EventHandler
  public void onPoweredBlockBreak(CustomBlockBreakEvent event) {
    Location location = event.getLocation();
    if(powerGrids.containsKey(location)) { //GRID / Power connector stuff
      if(powerGrids.get(location).getGridSize() > 1){
        List<PowerGridManager> newGrids = powerGrids.get(location).splitGrids(location);
        for(Location l: powerGrids.get(location).powerConnectors.keySet()) {
          powerGrids.remove(l);
        }
        for(PowerGridManager grid: newGrids){
          for(Location loc: grid.powerConnectors.keySet()){
            powerGrids.put(loc, grid);
          }
        }
      }
      powerGrids.remove(location);
    }

    if (!poweredBlocks.containsKey(location)) {
      return;
    }
    // Drop items
    PoweredBlock b = poweredBlocks.get(location);
    World world = location.getWorld();
    Inventory inventory = b.getInventory();
    ItemStack item;
    for (Integer i : b.getInteractableSlots()) {
      item = inventory.getItem(i);
      if (item != null) {
        world.dropItemNaturally(location, item);
      }
    }

    if (isReceiver(location)) {
      updateAdjacentProviders(location, false, PoweredBlockType.MACHINE);
    }
    Craftory.tickableBaseManager.removeUpdate(getPoweredBlock(location));
    removePoweredBlock(location);
  }

  @EventHandler
  public void onWrenchLeftClick(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    if (!CustomItemManager.matchCustomItemName(e.getItem(), CoreHolder.Items.WRENCH)) {
      return;
    }
    e.setCancelled(true);

    //Show power levels
    if (isPoweredBlock(e.getClickedBlock().getLocation())) {
      PoweredBlock block = getPoweredBlock(e.getClickedBlock().getLocation());
      e.getPlayer().sendMessage(
          "Stored: " + block.getInfoEnergyStored() + " RE / " + block.getInfoEnergyCapacity()
              + " RE");
    } else if(powerGrids.containsKey(e.getClickedBlock().getLocation())) {
      e.getPlayer().sendMessage(powerGrids.get(e.getClickedBlock().getLocation()).toString());
      e.getPlayer().sendMessage(powerGrids.values().toString());
    }
  }

  @EventHandler
  public void onConfigurator(final PlayerInteractEvent e) {
    if (!CustomItemManager.matchCustomItemName(e.getItem(), CoreHolder.Items.CONFIGURATOR)) {
      return;
    }
    e.setCancelled(true);

    final Player player = e.getPlayer();
    if (e.getAction() == Action.RIGHT_CLICK_AIR && player.isSneaking()) {
      sidesConfigCopying.remove(player.getUniqueId());
      player.sendMessage("Removed Sides Config Copy Data");
    }

    if (isProvider(e.getClickedBlock().getLocation())) {
      BaseProvider provider = (BaseProvider) getPoweredBlock(e.getClickedBlock().getLocation());
      if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
        sidesConfigCopying.put(player.getUniqueId(), provider.getSideConfig());
        player.sendMessage("Copied Sides Config");
      } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        if (sidesConfigCopying.containsKey(player.getUniqueId())) {
          provider.setSidesConfig(sidesConfigCopying.get(player.getUniqueId()));
          player.sendMessage("Pasted Sides Config");
        } else {
          player.sendMessage("No Sides Config Data Found, Please Copy First");
        }
      }
    }
  }

  //TODO CLEAN UP
  private void updateAdjacentProviders(Location location, Boolean setTo, PoweredBlockType type) {
    Block block;
    Location blockLocation;
    for (BlockFace face : faces) {
      block = location.getBlock().getRelative(face);
      blockLocation = block.getLocation();
      if (Craftory.customBlockManager.isCustomBlock(blockLocation)) {
        if (poweredBlocks.containsKey(blockLocation) && isProvider(blockLocation)) {
          ((BaseProvider) getPoweredBlock(blockLocation))
              .updateOutputCache(face.getOppositeFace(), setTo);
        } else if (setTo && Craftory.customBlockManager.getCustomBlockName(blockLocation)
            == CoreHolder.Blocks.POWER_CONNECTOR) { //TODO fix type part - seperate
          switch (type) {
            case MACHINE:
              powerGrids.get(location).addMachine(location, blockLocation);
              break;
            case GENERATOR:
              powerGrids.get(location).addGenerator(location, blockLocation);
              break;
            case CELL:
              powerGrids.get(location).addPowerCell(location, blockLocation);
              break;
          }

        }
      }
    }
  }

  public void mergeGrids(PowerGridManager old, PowerGridManager merged) {
    for (HashMap.Entry<Location, PowerGridManager> entry : powerGrids.entrySet()) {
      if(entry.getValue().equals(old)){
        powerGrids.put(entry.getKey(),merged);
      }
    }
  }

  private void getAdjacentPowerBlocks(Location location, PowerGridManager powerGridManager) {
    Location blockLocation;
    for (BlockFace face : faces) {
      blockLocation = location.getBlock().getRelative(face).getLocation();
      if (Craftory.customBlockManager.isCustomBlock(blockLocation) && poweredBlocks.containsKey(blockLocation)) {
        if (isCell(blockLocation)) {
          powerGridManager.addPowerCell(location, blockLocation);
        } else if (isGenerator(blockLocation)) {
          powerGridManager.addGenerator(location, blockLocation);
        } else if (isMachine(blockLocation)) {
          powerGridManager.addMachine(location, blockLocation);
        }
      }
    }
  }

  public void print(Player player) {
    player.sendMessage(poweredBlocks.toString());
  }

  private void addPowerGridManager(Location location, PowerGridManager manger) {
    powerGridManagers.add(manger);
    powerGrids.put(location, manger);
    //TODO for every merge or place of a power connector
    //TODO when merge change this
  }

  public PowerGridManager getPowerGridManager(Location location) {
    return powerGrids.get(location);
  }

  @Override
  public void update(long worldTime) {
    if (worldTime % CoreHolder.FOUR_TICKS == 0) {
      //Generate HashMap of loaded chunks in worlds
      HashSet<Chunk> loadedChunks;
      for (World world : Bukkit.getWorlds()) {
        loadedChunks = new HashSet<>(Arrays.asList(world.getLoadedChunks()));
        loadedChunkWorlds.put(world, loadedChunks);
      }
    }

    //If in loaded chunk, call update
    try {
      poweredBlocks.forEach(((location, poweredBlock) -> {
        if (loadedChunkWorlds != null && loadedChunkWorlds.get(location.getWorld())
            .contains(location.getChunk())) {
          poweredBlock.update(worldTime);
        }
      }));
    } catch (NullPointerException e) {
      Logger.debug(e.toString());
    }

    for(PowerGridManager gridManager: new HashSet<PowerGridManager>(powerGrids.values())){
      gridManager.update(worldTime);
    }
  }

  private static class PowerBlockManagerData implements Serializable {

    private static final long serialVersionUID = 9999L;
    protected final HashMap<Location, PoweredBlock> poweredBlocks;
    protected final HashSet<PowerGridManager> powerGridManagers;

    public PowerBlockManagerData(HashMap<Location, PoweredBlock> poweredBlocks,
        HashSet<PowerGridManager> powerGridManagers) {
      this.poweredBlocks = poweredBlocks;
      this.powerGridManagers = powerGridManagers;
    }
  }

}
