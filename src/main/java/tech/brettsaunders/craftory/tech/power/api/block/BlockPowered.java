package tech.brettsaunders.craftory.tech.power.api.block;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyInfo;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public abstract class BlockPowered implements IEnergyInfo, IEnergyReceiver, ITickable, Listener,
    Externalizable {
  private static transient final long serialVersionUID = -1692723606529286331L;
  public static final BlockFace faces[] = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };
  protected EnergyStorage energyStorage = new EnergyStorage(0);
  protected Location location;

  public BlockPowered(Location location) {
    init();
    this.location = location;
    Craftory.getBlockPoweredManager().addPoweredBlock(location, this);
  }

  public BlockPowered() {
    init();
  }

  public void init() {
    Craftory.getInstance().getServer().getPluginManager().registerEvents(this, Craftory.getInstance());
    Craftory.tickableBaseManager.addBaseTickable(this);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(energyStorage);
    out.writeObject(location);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    energyStorage = (EnergyStorage) in.readObject();
    location = (Location) in.readObject();
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    if(event.getBlock().getLocation() == this.location) {
      Craftory.getBlockPoweredManager().removePoweredBlock(this.location);
      Craftory.tickableBaseManager.removeBaseTickable(this); //TODO add check this is removed
    }
  }

  protected boolean hasEnergy(int energy) {

    return energyStorage.getEnergyStored() >= energy;
  }

  protected int getEnergySpace() {

    return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
  }

  /* GUI METHODS */
  public IEnergyStorage getEnergyStorage() {

    return energyStorage;
  }

  /* NBT METHODS */
  public void readFromNBT(NBTCompound nbt) {
    energyStorage.readFromNBT(nbt);
  }

  public NBTCompound writeToNBT(NBTCompound nbt) {
    energyStorage.writeToNBT(nbt);
    return nbt;
  }

  /* IEnergyInfo */
  @Override
  public int getInfoEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoMaxEnergyPerTick() {
    return 0;
  }

  @Override
  public int getInfoEnergyStored() {

    return energyStorage.getEnergyStored();
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(BlockFace from, int maxReceive, boolean simulate) {

    return energyStorage.receiveEnergy(maxReceive, simulate);
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

    return energyStorage.getMaxEnergyStored() > 0;
  }

  @Override
  public void updateNeighbourProviders() {
    for(BlockFace face : faces) {
      if (Craftory.getBlockPoweredManager().isPowerProvider(this.location.getBlock().getRelative(face).getLocation())) {
        IEnergyProvider provider = (IEnergyProvider) Craftory.getBlockPoweredManager().getPoweredBlock(this.location.getBlock().getRelative(face).getLocation());
        provider.updateOutputCache(face.getOppositeFace());
      }
    }
  }
}
