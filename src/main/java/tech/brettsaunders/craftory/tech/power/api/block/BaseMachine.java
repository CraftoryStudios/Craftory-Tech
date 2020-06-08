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
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver, Externalizable {

  /* Static Constants Protected */
  protected int amountReceive;
  /* Static Constants Private */
  private static final long serialVersionUID = 10007L;

  /* Per Object Variables Saved */


  /* Per Object Variables Not-Saved */


  /* Construction */
  public BaseMachine(Location location, byte level, int amountReceive) {
    super(location, level);
    this.amountReceive = amountReceive;
    init();
    if (ItemsAdder.areItemsLoaded()) {
      setupGUI();
    }
  }

  /* Saving, Setup and Loading */
  public BaseMachine() {
    super();
    init();

  }

  /* Common Load and Construction */
  private void init() {
    isReceiver = true;
    energyStorage.setMaxReceive(amountReceive);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, amountReceive), simulate);
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

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int maxReceiveEnergy() {
    return amountReceive;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Machine", new FontImageWrapper("extra:cell"));
    addGUIComponent(new GBattery(inventory, energyStorage));
  }

  public int getEnergyNeeded() {
    return Math.min(amountReceive, (energyStorage.capacity - energyStorage.energy));
  }
}
