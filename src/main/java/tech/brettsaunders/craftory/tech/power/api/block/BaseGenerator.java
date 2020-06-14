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
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseGenerator extends BaseProvider implements Externalizable {

  /* Static Constants Protected */
  protected static final int CAPACITY_BASE = 40000;
  protected static final double[] CAPACITY_LEVEL = {1, 1.5, 2, 3};
  /* Static Constants Private */
  private static final long serialVersionUID = 10006L;
  /* Per Object Variables Saved */
  protected int fuelRE;

  /* Per Object Variables Not-Saved */
  protected transient VariableContainer<Boolean> runningContainer;
  protected transient int maxFuelRE;
  protected transient int lastEnergy;
  protected transient boolean isActive;
  protected transient boolean wasActive;

  /* Construction */
  public BaseGenerator(Location location, byte level, int outputAmount) {
    super(location, level, outputAmount);
    energyStorage = new EnergyStorage((int) (CAPACITY_BASE * CAPACITY_LEVEL[level]));
    init();
    if (ItemsAdder.areItemsLoaded()) {
      setupGUI();
    }
  }

  /* Saving, Setup and Loading */
  public BaseGenerator() {
    super();
    init();
    wasActive = false;
    lastEnergy = 0;
    maxFuelRE = 0;
  }

  /* Common Load and Construction */
  private void init() {
    isActive = false;
    isProvider = true;
    runningContainer = new VariableContainer<>(false);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(fuelRE);
    out.writeObject(energyStorage);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    fuelRE = in.readInt();
    energyStorage = (EnergyStorage) in.readObject();
  }

  /* Update Loop */
  @Override
  public void update(long worldTime) {
    super.update(worldTime);
    if (isActive) {
      processTick();
      if (canFinish()) {
        if (!canStart()) {
          processOff();
        } else {
          processStart();
        }
      }
    } else {
      if (timeCheck()) {
        if (canStart()) {
          processStart();
          processTick();
          isActive = true;
        }
      }
    }
    if (timeCheck()) {
      if (!isActive) {
        processIdle();
      }
    }
  }

  /* Internal Helper Functions */
  protected boolean timeCheck() {
    //Time check factor to slow down check speed;
    //Core Props
    //return world.getTotalWorldTime() % TIME_CONSTANT == 0;
    return true;
  }

  protected abstract boolean canStart();

  protected boolean canFinish() {
    return fuelRE <= 0;
  }

  protected void processStart() {
    runningContainer.setT(true);
  }

  protected void processFinish() {
  }

  protected void processIdle() {
  }

  protected void processOff() {
    isActive = false;
    wasActive = true;
    runningContainer.setT(false);
  }

  protected void processTick() {
    lastEnergy = getMaxOutput();
    energyStorage.modifyEnergyStored(lastEnergy); //TODO need to fix look at old code
    fuelRE -= lastEnergy;
  }

  /* External Methods */
  public final void setEnergyStored(int quantity) {
    energyStorage.setEnergyStored(quantity);
  }

  /* IEnergyInfo */
  @Override
  public int getInfoEnergyPerTick() {
    if (!isActive) {
      return 0;
    }
    return lastEnergy;
  }

  @Override
  public int getInfoEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Generator", new FontImageWrapper("extra:cell"));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig));
  }

}
