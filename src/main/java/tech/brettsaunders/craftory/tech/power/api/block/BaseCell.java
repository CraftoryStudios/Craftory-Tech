package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseCell extends BaseProvider implements IEnergyReceiver, Externalizable {

  protected static final int CAPACITY_BASE = 400000;
  protected static final int[] CAPACITY_LEVEL = {1, 5, 50, 200};
  protected static final int MAX_INPUT = 200;
  protected static final int[] INPUT_LEVEL = {1, 4, 40, 160};
  /* Static Constants */
  private static final long serialVersionUID = 10004L;

  /* Construction */
  public BaseCell(Location location, byte level, int outputAmount) {
    super(location, level, outputAmount);
    energyStorage = new EnergyStorage(CAPACITY_BASE * CAPACITY_LEVEL[level]);
    isReceiver = true;
    isProvider = true;
    if (ItemsAdder.areItemsLoaded()) {
      setupGUI();
    }
  }

  /* Saving, Setup and Loading */
  public BaseCell() {
    super();
    isReceiver = true;
    isProvider = true;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(energyStorage);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    energyStorage = (EnergyStorage) in.readObject();
  }

  /* Update Loop */
  @Override
  public void fastUpdate() {
    super.fastUpdate();
    transferEnergy();
  }

  /* IEnergyReciever */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage
        .receiveEnergy(Math.min(maxReceive, MAX_INPUT * INPUT_LEVEL[level]), simulate);
  }

  @Override
  public int getEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return energyStorage.getMaxEnergyStored();
  }

  @Override
  public int getEnergySpace() {
    return Math.max(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(), MAX_INPUT * INPUT_LEVEL[level]);
  }

  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Cell", new FontImageWrapper("extra:cell"));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig));
  }
}
