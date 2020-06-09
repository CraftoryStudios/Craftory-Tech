package tech.brettsaunders.craftory.tech.power.core.manager;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
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
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelGenerator;
import tech.brettsaunders.craftory.utils.Blocks;
import tech.brettsaunders.craftory.utils.Blocks.Power;
import tech.brettsaunders.craftory.utils.Items;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredBlockManager implements Listener, ITickable {

  public static final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
  private static final String DATA_PATH =
      Craftory.getInstance().getDataFolder().getPath() + File.separator + "PowerBlockManager.data";
  private final HashMap<Location, PowerGridManager> powerConnectors;
  public HashSet<PowerGridManager> powerGridManagers;
  private HashMap<Location, PoweredBlock> poweredBlocks;

  public PoweredBlockManager() {
    poweredBlocks = new HashMap<>();
    powerGridManagers = new HashSet<>();
    powerConnectors = new HashMap<>();
    Craftory.getInstance().getServer().getPluginManager()
        .registerEvents(this, Craftory.getInstance());
    Craftory.tickableBaseManager.addFastUpdate(this);
  }

  public void onEnable() {
    load();
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
      Logger.info("PowerBlockManager Loaded");
    } catch (FileNotFoundException e) {
      Logger.debug("First Run - Generating PowerBlockManager Data");
    } catch (IOException e) {
      Logger.error("PowerBlockManager IO Loading Issue");
      Logger.debug(e);
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      PowerBlockManagerData data = new PowerBlockManagerData(poweredBlocks, powerGridManagers);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(DATA_PATH)));
      out.writeObject(data);
      out.close();
      Logger.info("PowerBlockManager Data Saved");
    } catch (IOException e) {
      Logger.warn("Couldn't save PowerBlockManager Data");
      Logger.debug(e);
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
    save();
  }

  @EventHandler
  public void onItemsAdderLoaded(ItemsAdderFirstLoadEvent e) {
    poweredBlocks.forEach(((location, poweredBlock) -> {
      poweredBlock.setupGUI();
    }));
  }

  @EventHandler
  public void onGUIBlockClick(CustomBlockInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if (poweredBlocks.containsKey(e.getBlockClicked().getLocation())) {
      //Open GUI of Powered Block
      poweredBlocks.get(e.getBlockClicked().getLocation()).openGUI(e.getPlayer());
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
  public void onPoweredBlockPlace(BlockPlaceEvent event) {
    Location location = event.getBlockPlaced().getLocation();
    Craftory.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
        Craftory.getInstance(), new Runnable() {
          @Override
          public void run() {
            PoweredBlock poweredBlock = null;
            PoweredBlockType type = PoweredBlockType.MACHINE;
            if (!ItemsAdder.isCustomBlock(event.getBlockPlaced())) {
              return;
            }

            ItemStack blockPlacedItemStack = ItemsAdder.getCustomBlock(event.getBlockPlaced());
            String blockPlacedName = ItemsAdder.getCustomItemName(blockPlacedItemStack);

            switch (blockPlacedName) {

              case Power.IRON_CELL:
                poweredBlock = new IronCell(location);
                type = PoweredBlockType.CELL;
                break;
              case Power.GOLD_CELL:
                poweredBlock = new GoldCell(location);
                type = PoweredBlockType.CELL;
                break;
              case Power.DIAMOND_CELL:
                poweredBlock = new DiamondCell(location);
                type = PoweredBlockType.CELL;
                break;
              case Power.EMERALD_CELL:
                poweredBlock = new EmeraldCell(location);
                type = PoweredBlockType.CELL;
                break;

              case Blocks.Power.SOLID_FUEL_GENERATOR:
                poweredBlock = new SolidFuelGenerator(location);
                type = PoweredBlockType.GENERATOR;
                break;
              case Blocks.Power.POWER_CONNECTOR:
                PowerGridManager manager = new PowerGridManager(location);
                getAdjacentPowerBlocks(location, manager);
                addPowerGridManager(location, manager);
                type = PoweredBlockType.CELL;
                break;
              case Power.IRON_ELECTRIC_FURNACE:
                poweredBlock = new IronElectricFurnace(location);
                type = PoweredBlockType.MACHINE;
                break;
              case Power.GOLD_ELECTRIC_FURNACE:
                poweredBlock = new GoldElectricFurnace(location);
                type = PoweredBlockType.MACHINE;
                break;
              case Power.EMERALD_ELECTRIC_FURNACE:
                poweredBlock = new EmeraldElectricFurnace(location);
                type = PoweredBlockType.MACHINE;
                break;
              case Power.DIAMOND_ELECTRIC_FURNACE:
                poweredBlock = new DiamondElectricFurnace(location);
                type = PoweredBlockType.MACHINE;
                break;
              default:
                return;
            }

            //Carry out PoweredBlock Base Setup
            if (poweredBlock != null) {
              addPoweredBlock(location, poweredBlock);
              if (poweredBlock.isReceiver()) {
                updateAdjacentProviders(location, true, type);
              }
            }
          }
        }, 1L);
  }

  @EventHandler
  public void onPoweredBlockBreak(CustomBlockBreakEvent event) {
    Location location = event.getBlock().getLocation();
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
    Craftory.tickableBaseManager.removeFastUpdate(getPoweredBlock(location));
    removePoweredBlock(location);
  }

  @EventHandler
  public void onWrenchLeftClick(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    if (!ItemsAdder.matchCustomItemName(e.getItem(), Items.Power.WRENCH)) {
      return;
    }
    e.setCancelled(true);

    //Show power levels
    if (isPoweredBlock(e.getClickedBlock().getLocation())) {
      PoweredBlock block = getPoweredBlock(e.getClickedBlock().getLocation());
      e.getPlayer().sendMessage(
          "Stored: " + block.getInfoEnergyStored() + " / " + block.getInfoEnergyCapacity());
    }
  }

  //TODO CLEAN UP
  private void updateAdjacentProviders(Location location, Boolean setTo, PoweredBlockType type) {
    Block block;
    for (BlockFace face : faces) {
      block = location.getBlock().getRelative(face);
      if (ItemsAdder.isCustomBlock(block)) {
        if (poweredBlocks.containsKey(block.getLocation()) && isProvider(block.getLocation())) {
          ((BaseProvider) getPoweredBlock(block.getLocation()))
              .updateOutputCache(face.getOppositeFace(), setTo);
        } else if (setTo && ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(block))
            == Power.POWER_CONNECTOR) { //TODO fix type part - seperate
          switch (type) {
            case MACHINE:
              powerConnectors.get(location).addMachine((BaseMachine) getPoweredBlock(location));
              break;
            case GENERATOR:
              powerConnectors.get(location).addGenerator((BaseGenerator) getPoweredBlock(location));
              break;
            case CELL:
              powerConnectors.get(location).addPowerCell((BaseCell) getPoweredBlock(location));
              break;
          }

        }
      }
    }
  }

  private void getAdjacentPowerBlocks(Location location, PowerGridManager powerGridManager) {
    Block block;
    for (BlockFace face : faces) {
      block = location.getBlock().getRelative(face);
      if (ItemsAdder.isCustomBlock(block) && poweredBlocks.containsKey(block.getLocation())) {
        if (isCell(location)) {
          powerGridManager.addPowerCell((BaseCell) getPoweredBlock(location));
        } else if (isGenerator(location)) {
          powerGridManager.addGenerator((BaseGenerator) getPoweredBlock(location));
        } else if (isMachine(location)) {
          powerGridManager.addMachine((BaseMachine) getPoweredBlock(location));
        }
      }
    }
  }

  public void print(Player player) {
    player.sendMessage(poweredBlocks.toString());
  }

  private void addPowerGridManager(Location location, PowerGridManager manger) {
    powerGridManagers.add(manger);
    powerConnectors.put(location, manger);
    //TODO for every merge or place of a power connector
    //TODO when merge change this
  }

  public PowerGridManager getPowerGridManager(Location location) {
    return powerConnectors.get(location);
  }

  @Override
  public void update() {
    //Generate HashMap of loaded chunks in worlds
    HashMap<World, HashSet> loadedChunkWorlds = new HashMap<>();
    HashSet<Chunk> loadedChunks;
    for (World world : Bukkit.getWorlds()) {
      loadedChunks = new HashSet<>(Arrays.asList(world.getLoadedChunks()));
      loadedChunkWorlds.put(world, loadedChunks);
    }

    //If in loaded chunk, call update
    poweredBlocks.forEach(((location, poweredBlock) -> {
      if (loadedChunkWorlds.get(location.getWorld()).contains(location.getChunk())) {
        poweredBlock.update();
      }
    }));
  }


  private static class PowerBlockManagerData implements Serializable {

    private static final long serialVersionUID = 9999L;
    protected HashMap<Location, PoweredBlock> poweredBlocks;
    protected HashSet<PowerGridManager> powerGridManagers;

    public PowerBlockManagerData(HashMap<Location, PoweredBlock> poweredBlocks,
        HashSet<PowerGridManager> powerGridManagers) {
      this.poweredBlocks = poweredBlocks;
      this.powerGridManagers = powerGridManagers;
    }
  }

}
