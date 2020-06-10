package tech.brettsaunders.craftory.tech.power.api.block;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver, Externalizable {

  /* Static Constants Protected */

  /* Static Constants Private */
  private static final long serialVersionUID = 10007L;
  /* Per Object Variables Saved */
  protected int maxReceive;

  /* Per Object Variables Not-Saved */
  protected transient VariableContainer<Boolean> runningContainer;
  protected transient VariableContainer<Double> progressContainer;
  protected transient int processTime;
  protected transient int energyConsumption;
  protected transient int tickCount = 0;

  /* Construction */
  public BaseMachine(Location location, byte level, int maxReceive) {
    super(location, level);
    this.maxReceive = maxReceive;
    energyStorage.setMaxReceive(maxReceive);
    init();
  }

  /* Saving, Setup and Loading */
  public BaseMachine() {
    super();
    init();
  }

  /* Common Load and Construction */
  private void init() {
    isReceiver = true;
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
  }


  /* Update Loop */
  @Override
  public void fastUpdate() {
    super.fastUpdate();
    if (inventoryInterface == null) {
      return;
    }
    updateSlots();
    if (validateContense() && energyStorage.getEnergyStored() >= energyConsumption) {
      runningContainer.setT(true);
      energyStorage.modifyEnergyStored(-energyConsumption);
      tickCount += 1;
      if (tickCount == processTime) {
        tickCount = 0;
        processComplete();
      }
    } else {
      runningContainer.setT(false);
    }
    progressContainer.setT(((double) tickCount) / processTime);
  }

  protected  abstract void processComplete();
  protected abstract boolean validateContense();
  protected abstract void updateSlots();

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(maxReceive);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    maxReceive = in.readInt();
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, this.maxReceive), simulate);
  }

  /* IEnergyHandler */
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
    return Math.max(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(), maxReceive);
  }

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int maxReceiveEnergy() {
    return maxReceive;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Machine", new FontImageWrapper("extra:cell"));
    addGUIComponent(new GBattery(inventory, energyStorage));
  }
}
