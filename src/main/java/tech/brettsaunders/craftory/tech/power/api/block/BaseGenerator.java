package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.VariableContainer;

public abstract class BaseGenerator extends BaseProvider implements IHopperInteract {

  /* Static Constants Protected */
  protected static final int CAPACITY_BASE = 40000;
  protected static final double[] CAPACITY_LEVEL = {1, 1.5, 2, 3};
  protected static final int FUEL_SLOT = 22;
  /* Per Object Variables Saved */
  @Persistent
  protected int fuelRE;

  /* Per Object Variables Not-Saved */
  protected transient VariableContainer<Boolean> runningContainer;
  @Persistent
  protected int maxFuelRE;
  @Persistent
  protected int lastEnergy;
  protected boolean isActive;
  protected boolean wasActive;

  private static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, FUEL_SLOT);
      put(BlockFace.EAST, FUEL_SLOT);
      put(BlockFace.SOUTH, FUEL_SLOT);
      put(BlockFace.WEST, FUEL_SLOT);
      put(BlockFace.UP, FUEL_SLOT);
    }
  };

  private static final HashMap<BlockFace, Integer> outputFaces = new HashMap<>();

  /* Construction */
  public BaseGenerator(Location location, String blockName, byte level, int outputAmount) {
    super(location, blockName, level, outputAmount);
    energyStorage = new EnergyStorage((int) (CAPACITY_BASE * CAPACITY_LEVEL[level]));
    init();
  }

  /* Saving, Setup and Loading */
  public BaseGenerator() {
    super();
    isActive = false;
    wasActive = true;
    init();
  }

  /* Common Load and Construction */
  private void init() {
    isActive = false;
    runningContainer = new VariableContainer<>(false);
  }

  /* Update Loop */
  @Ticking(ticks = 1)
  public void updateGenerator() {
    if (isBlockPowered()) return;
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
    energyStorage.modifyEnergyStored(lastEnergy);
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
    Inventory inventory = createInterfaceInventory(displayName, Font.GENERATOR_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig));
  }

  @Override
  public HashMap<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }

  @Override
  public HashMap<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }

}
