package tech.brettsaunders.craftory.tech.power.core.block;


import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.helpers.MathHelper;
import tech.brettsaunders.craftory.tech.power.api.block.BlockPowered;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.utils.Logger;

public class BlockCell extends BlockPowered implements IEnergyProvider, Externalizable {

  private static transient final long serialVersionUID = -1692728206529286331L;

  public static final int CAPACITY_BASE = 2000000;
  public static final int[] CAPACITY = new int[]{1, 4, 9, 16, 25};
  public static final int XFER_BASE = 1000;
  public static final int[] XFER_SCALE = { 1, 4, 9, 16, 25 };
  public static final int[] RECV = { 1, 4, 9, 16, 25 };
  public static final int[] SEND = { 1, 4, 9, 16, 25 };
  public static final Integer[] DEFAULT_SIDES_CONFIG = { 0, 0, 0, 0, 0, 0 };  //NORTH, EAST, SOUTH, WEST, UP, DOWN
  public static final int CONFIG_NONE = 0;
  public static final int CONFIG_OUTPUT = 1;
  public static final int CONFIG_INPUT = 2;
  public static FontImageWrapper fontImageWrapper;
  public static TexturedInventoryWrapper inventory;

  protected boolean enabled = true;
  protected ArrayList<Integer> sidesConfig = new ArrayList<>(6);
  protected ArrayList<Boolean> sidesCache = new ArrayList<>(6);
  public int amountSend;
  public int amountReceive;
  protected int level;


  public BlockCell(Location location) {
    super(location);
    Logger.warn(location.toString());
    Logger.warn(this.location.toString());
    init();
    level = 0;
    energyStorage = new EnergyStorage(getMaxCapacity(0));
    Collections.addAll(sidesConfig, DEFAULT_SIDES_CONFIG);
    generateSideCache();
    energyStorage.setEnergyStored(1000);
  }

  public BlockCell() {
    init();
  }

  public void init() {
    fontImageWrapper = new FontImageWrapper("mcguis:blank_menu");
    inventory = new TexturedInventoryWrapper(null,
        54,
        ChatColor.BLACK + "   Cell",fontImageWrapper);
  }

  public void showInterface(Player player) {
    inventory.showInventory(player);
    inventory.getInternal().setItem(17, ItemsAdder.getCustomItem("extra:output"));
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeBoolean(enabled);
    out.writeObject(sidesConfig);
    out.writeObject(sidesCache);
    out.writeInt(amountSend);
    out.writeInt(amountReceive);
    out.writeInt(level);
    out.writeObject(energyStorage);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    enabled = in.readBoolean();
    sidesConfig = (ArrayList<Integer>) in.readObject();
    sidesCache = (ArrayList<Boolean>) in.readObject();
    amountSend = in.readInt();
    amountReceive = in.readInt();
    level = in.readInt();
    energyStorage = (EnergyStorage) in.readObject();
  }

  public static void initialize() {
    int capacity = CAPACITY_BASE;
    int receive = XFER_BASE;
    int send = XFER_BASE;
    //TODO OR config values

    //Calculate different capacities for different levels
    for (int i = 0; i < CAPACITY.length; i++) {
      CAPACITY[i] *= capacity;
      RECV[i] *= receive;
      SEND[i] *= send;
    }
  }

  public static int getMaxCapacity(int level) {
    return (int) Math.max(0, CAPACITY[MathHelper.clamp(level, 0, 4)]);
  }

  protected boolean setLevel(int level) {
    int curLevel = this.level;
    energyStorage.setCapacity(getMaxCapacity(level));
    amountReceive = amountReceive * XFER_SCALE[level] / XFER_SCALE[curLevel];
    amountSend = amountSend * XFER_SCALE[level] / XFER_SCALE[curLevel];
    return true;
  }

  //TODO IUpgradable

  @Override
  public void update() {
    if (enabled) {
      transferEnergy();
    }
  }

  protected void transferEnergy() {
    for (int i = 0; i < sidesConfig.size(); i++) {
      if (sidesConfig.get(i) == CONFIG_OUTPUT && sidesCache.get(i)) {
        energyStorage.modifyEnergyStored(-insertEnergyIntoAdjacentEnergyReceiver(i, Math.min(amountSend, energyStorage.getEnergyStored()), false));
      }
    }
  }

  /* IEnergyProvider */
  @Override
  public boolean updateOutputCache(BlockFace inputFrom) {
    //NORTH, EAST, SOUTH, WEST, UP, DOWN
    int side = -1;
    switch (inputFrom) {
      case NORTH: side = 0;
      break;
      case EAST: side = 1;
      break;
      case SOUTH: side = 2;
      break;
      case WEST: side = 3;
      break;
      case UP: side = 4;
      break;
      case DOWN: side = 5;
      break;
    }
    if (side != -1) {
      sidesCache.set(side, true);
      return true;
    }
    return false;
  }

  /* IEnergyReceiver */
  @Override
  public int extractEnergy(BlockFace from, int maxExtract, boolean simulate) {
      return energyStorage.extractEnergy(Math.min(maxExtract, amountSend), simulate);
  }

  @Override
  public int receiveEnergy(BlockFace from, int maxReceive, boolean simulate) {
      return energyStorage.receiveEnergy(Math.min(maxReceive, amountReceive), simulate);
  }

  @Override
  public int getEnergyStored(BlockFace from) {

    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(BlockFace from) {

    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(BlockFace from) {

    return true;
  }

  /* BlockHelpers */
  public int insertEnergyIntoAdjacentEnergyReceiver(int side, int energy, boolean simulate) {
    Location targetLocation = this.location.getBlock().getRelative(faces[side]).getLocation();
    if (Craftory.getPoweredBlockManager().isPowerReciever(targetLocation)) {
      return Craftory.getPoweredBlockManager().getPoweredBlock(targetLocation).receiveEnergy(BlockFace.EAST, energy, simulate);
    } else {
      sidesCache.set(side, false);
    }
    return 0;
  }

  //TODO on block place add to cache
  private void generateSideCache() {
    int i = 0;
    for(BlockFace face : faces) {
      if (Craftory.getPoweredBlockManager().isPowerReciever(this.location.getBlock().getRelative(face).getLocation())) {
        sidesCache.add(i, true);
        Logger.info("Cached side " + i);
      } else {
        sidesCache.add(i, false);
      }
      i++;
    }
  }
}
