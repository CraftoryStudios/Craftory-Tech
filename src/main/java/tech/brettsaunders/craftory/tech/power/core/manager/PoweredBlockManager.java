package tech.brettsaunders.craftory.tech.power.core.manager;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.core.block.IronCell;
import tech.brettsaunders.craftory.tech.power.core.block.SolidFuelGenerator;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredBlockManager implements Listener {

  private static final String DATA_PATH = Craftory.getInstance().getDataFolder().getPath() + File.separator + "PowerBlockManager.data";
  public static final BlockFace faces[] = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

  private HashMap<Location, PoweredBlock> poweredBlocks;

  public PoweredBlockManager() {
    poweredBlocks = new HashMap<>();
    init();
  }

  public void onEnable() {
    load();
  }

  public void onDisable() {
    save();
  }

  public void init() {
    Craftory.getInstance().getServer().getPluginManager().registerEvents(this, Craftory.getInstance());
  }

  public void addPoweredBlock(Location location, PoweredBlock blockPowered) {
    poweredBlocks.put(location, blockPowered);
  }

  public PoweredBlock getPoweredBlock(Location location) {
    return poweredBlocks.get(location);
  }

  public boolean isPoweredBlock(Location location) {return poweredBlocks.containsKey(location);}

  public void removePoweredBlock(Location location) {
    poweredBlocks.remove(location);
  }

  public void load() {
    init();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(DATA_PATH)));
      PowerBlockManagerData data = (PowerBlockManagerData) in.readObject();
      poweredBlocks = data.poweredBlocks;
      in.close();
      Logger.info("PowerBlockManager Loaded");
    } catch (IOException | ClassNotFoundException e) {
      Logger.warn("New PowerBlockManager Data Created");
      Logger.debug(e.toString());
    }
  }

  public void save() {
    try {
      PowerBlockManagerData data = new PowerBlockManagerData(poweredBlocks);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(DATA_PATH)));
      out.writeObject(data);
      out.close();
      Logger.info("PowerBlockManager Data Saved");
    } catch (IOException e) {
      Logger.warn("Couldn't save PowerBlockManager Data");
      Logger.debug(e.toString());
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
    save();
  }

  @EventHandler
  public void onGUIOpen(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (poweredBlocks.containsKey(event.getClickedBlock().getLocation())) {
        //Open GUI of Powered Block
        poweredBlocks.get(event.getClickedBlock().getLocation()).openGUI(event.getPlayer());
      }
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

  @EventHandler
  public void onPoweredBlockPlace(BlockPlaceEvent event) {
    Location location = event.getBlockPlaced().getLocation();
    Craftory.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Craftory.getInstance(),
        () -> {
          PoweredBlock poweredBlock;
          if (!ItemsAdder.isCustomBlock(event.getBlockPlaced())) return;

          switch (ItemsAdder.getCustomItemName(ItemsAdder.getCustomBlock(event.getBlockPlaced()))) {
            case "craftory:power_cell":
              poweredBlock = new IronCell(location);
              break;
            case "craftory:soild_fuel_generator":
              poweredBlock = new SolidFuelGenerator(location);
              break;
            default:
              return;
          }

          addPoweredBlock(location, poweredBlock);
          if (poweredBlock.isReceiver()) {
            updateAdjacentProviders(location, true);
          }
        }, 1L);
  }

  @EventHandler
  public void onPoweredBlockBreak(BlockBreakEvent event) {
    Location location = event.getBlock().getLocation();
    if (!poweredBlocks.containsKey(location)) return;
    if (isReceiver(location)) {
      updateAdjacentProviders(location, false);
    }
    Craftory.tickableBaseManager.removeBaseTickable(getPoweredBlock(location));
    removePoweredBlock(location);
  }

  private void updateAdjacentProviders(Location location, Boolean setTo) {
    Block block;
    for (BlockFace face : faces) {
      block = location.getBlock().getRelative(face);
      if (ItemsAdder.isCustomBlock(block) && poweredBlocks.containsKey(block.getLocation()) && isProvider(block.getLocation())) {
        ((BaseProvider) getPoweredBlock(block.getLocation())).updateOutputCache(face.getOppositeFace(), setTo);
      }
    }
  }

  public void print(Player player) {
    player.sendMessage(poweredBlocks.toString());
  }

  private static class PowerBlockManagerData implements Serializable {
    private static transient final long serialVersionUID = -1692723206529286331L;
    protected HashMap<Location, PoweredBlock> poweredBlocks;
    public PowerBlockManagerData(HashMap<Location, PoweredBlock> poweredBlocks) {
      this.poweredBlocks = poweredBlocks;
    }
  }

}
