package tech.brettsaunders.craftory.tech.power.api.block;


import tech.brettsaunders.craftory.tech.helpers.MathHelper;
import tech.brettsaunders.craftory.tech.power.api.interfaces.EnumFacing;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class BlockCell extends BlockPowered implements ITickable, IEnergyProvider {

  public static final int CAPACITY_BASE = 2000000;
  public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
  public static final int XFER_BASE = 1000;
  public static final int[] XFER_SCALE = { 1, 4, 9, 16, 25 };
  public static final int[] RECV = { 1, 4, 9, 16, 25 };
  public static final int[] SEND = { 1, 4, 9, 16, 25 };
  public static final int[] DEFAULT_SIDES_CONFIG = { 0, 0, 0, 0, 0, 0 };  //NORTH, EAST, SOUTH, WEST, UP, DOWN
  public static final int CONFIG_NONE = 0;
  public static final int CONFIG_OUTPUT = 1;
  public static final int CONFIG_INPUT = 2;

  protected boolean enabled = true;
  protected int[] sidesConfig;
  public int amountSend;
  public int amountReceive;

  protected int level;


  public BlockCell() {
    super();
    energyStorage = new EnergyStorage(getMaxCapacity(0));
    sidesConfig = DEFAULT_SIDES_CONFIG;
  }

  public static void initialize() {
    int capacity = CAPACITY_BASE;
    int receive = XFER_BASE;
    int send = XFER_BASE;
    //OR config value

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
    for (int i = 0; i < sidesConfig.length; i++) {
      if (sidesConfig[i] == CONFIG_OUTPUT) {
        energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, i, Math.min(amountSend, energyStorage.getEnergyStored()), false));
      }
    }
  }

  /* IEnergyReceiver */
  @Override
  public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
      return energyStorage.extractEnergy(Math.min(maxExtract, amountSend), simulate);
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
      return energyStorage.receiveEnergy(Math.min(maxReceive, amountReceive), simulate);
  }

  @Override
  public int getEnergyStored(EnumFacing from) {

    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {

    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {

    return true;
  }
}
