package tech.brettsaunders.craftory.tech.power.core.manager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.BaseProvider;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;

public class PowerGridManager implements Externalizable, ITickable {

  private static final long serialVersionUID = 10021L;
  public final HashMap<Location, HashSet<Location>> powerConnectors = new HashMap<>();
  private HashMap<Location, HashSet<Location>> machineConnections = new HashMap<>();
  private HashSet<BaseCell> cells = new HashSet<>();
  private HashSet<BaseProvider> generators = new HashSet<>();
  private HashSet<BaseMachine> machines = new HashSet<>();
  private int machinesNeedingEnergy = 0;

  public PowerGridManager(Location powerConnector) {
    addPowerConnector(powerConnector);
  }

  public PowerGridManager() {
  }

  public HashSet<BaseCell> getCells() {
    return cells;
  }

  public HashSet<BaseProvider> getGenerators() {
    return generators;
  }

  public HashSet<BaseMachine> getMachines() {
    return machines;
  }

  public int getGridSize() {
    return cells.size() + generators.size() + machines.size();
  }


  public void update(long worldTime) {
    int needed = whatDoTheyNeed();
    int cellCapacity = calculateStorageSpace();
    int produced = calculateEnergyProduced(needed + cellCapacity);
    if (needed > produced) {
      produced += raidTheBank(needed - produced);
    }
    if (produced > needed) {
      int extra = giveThePeopleWhatTheyWant(produced);
      fillTheBanks(extra);
    } else {
      shareThisAmongstThePeople(produced);
    }
  }

  private int calculateStorageSpace() {
    int amount = 0;
    for (BaseCell cell : cells) {
      amount += cell.getEnergySpace();
    }
    return amount;
  }

  /* Calculates how much energy the generators produced this tick */
  private int calculateEnergyProduced(int limit) {
    int amount = 0;
    int e;
    for (BaseProvider generator : generators) {
      e = generator.getEnergyAvailable();
      if (amount + e > limit) {
        e = limit - amount;
      }
      e = generator.retrieveEnergy(e);
      amount += e;
      if (amount == limit) {
        break;
      }
    }
    return amount;
  }

  /* Calculates how much energy the machines can take this tick */
  private int whatDoTheyNeed() {
    machinesNeedingEnergy = 0;
    int amount = 0;
    int e;
    for (BaseMachine machine : machines) {
      e = machine.getEnergySpace();
      if (e > 0) {
        amount += e;
        machinesNeedingEnergy += 1;
      }
    }
    return amount;
  }

  /**
   * Attempts to gather energy from storage
   *
   * @param goal amount of energy to gather
   * @return amount gathered
   */
  private int raidTheBank(int goal) {
    int amount = 0;
    for (BaseCell cell : cells) {
      amount += cell.retrieveEnergy((goal - amount));
      if (amount >= goal) {
        break;
      }
    }
    return amount;
  }

  /**
   * Puts excess energy into storage
   *
   * @param amount the amount of excess energy
   */
  private void fillTheBanks(int amount) {
    for (BaseCell cell : cells) {
      amount -= cell.receiveEnergy(amount, false);
    }
  }

  /* Provides the machines with energy.
   * Used when there is enough for all the machines  */
  private int giveThePeopleWhatTheyWant(int amount) {
    for (BaseMachine machine : machines) {
      amount -= machine.receiveEnergy(amount, false);
    }
    return amount;
  }

  /* Shares the available energy amongst the machines
   * Used when there is not enough for all machines  */
  private void shareThisAmongstThePeople(int amount) {
    int allotment = 1;
    if (machinesNeedingEnergy > 1) {
      allotment = amount / machinesNeedingEnergy;
    }
    while (amount > 0) {
      for (BaseMachine machine : machines) {
        amount -= machine.receiveEnergy(allotment, false);
      }
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(cells);
    out.writeObject(generators);
    out.writeObject(machines);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    cells = (HashSet<BaseCell>) in.readObject();
    generators = (HashSet<BaseProvider>) in.readObject();
    machines = (HashSet<BaseMachine>) in.readObject();
  }

  public void combineManagers(PowerGridManager other) {
    cells.addAll(other.getCells());
    generators.addAll(other.getGenerators());
    machines.addAll(other.getMachines());
    powerConnectors.putAll(other.powerConnectors);
  }

  /* Common Methods */
  public void addPowerConnector(Location location) {
    this.powerConnectors.put(location, new HashSet<>());
  }

  public void addPowerConnectorConnection(Location from, Location to) {
    HashSet<Location> temp = powerConnectors.get(from);
    temp.add(to);
    powerConnectors.replace(from, temp);
  }

  public void addPowerCell(BaseCell cell) {
    this.cells.add(cell);
  }

  public void addMachine(BaseMachine machine) {
    this.machines.add(machine);
  }

  public void addGenerator(BaseGenerator generator) {
    this.generators.add(generator);
  }
}
