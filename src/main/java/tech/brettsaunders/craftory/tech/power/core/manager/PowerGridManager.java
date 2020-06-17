package tech.brettsaunders.craftory.tech.power.core.manager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.interfaces.ITickable;
import tech.brettsaunders.craftory.tech.power.core.block.cell.IronCell;
import tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace.IronElectricFurnace;
import tech.brettsaunders.craftory.tech.power.core.block.machine.generators.SolidFuelGenerator;

public class PowerGridManager implements Externalizable, ITickable {

  private static final long serialVersionUID = 10021L;
  public final HashMap<Location, HashSet<Location>> powerConnectors = new HashMap<>();
  protected final HashMap<Location, HashSet<Location>> blockConnections = new HashMap<>();
  private HashSet<Location> cells = new HashSet<>();
  private HashSet<Location> generators = new HashSet<>();
  private HashSet<Location> machines = new HashSet<>();
  private int machinesNeedingEnergy = 0;

  public PowerGridManager(Location powerConnector) {
    addPowerConnector(powerConnector);
  }

  public PowerGridManager() {
  }

  /**
   * Splits a power grid into the needed amount of new grids based on the power connector that broke the grid
   * @param breakPoint The location of the broken power connector
   * @return A list of the individual grids (could just be one)
   */
  public ArrayList<PowerGridManager> splitGrids(Location breakPoint) {
    ArrayList<PowerGridManager> managers = new ArrayList<>();
    blockConnections.remove(breakPoint);
    HashSet<Location> neighbours = powerConnectors.remove(breakPoint);
    HashSet<Location> closedSet = new HashSet<>();
    for(Location location: neighbours) { //Loop through all the neighbours of broken connector
      if(closedSet.contains(location)) continue; //If this neighbour isnt part of one of the new grids make one
      PowerGridManager grid = new PowerGridManager();
      HashSet<Location> connections = powerConnectors.get(location);
      connections.remove(breakPoint);
      grid.powerConnectors.put(location, connections);
      grid.blockConnections.put(location, blockConnections.get(location));
      closedSet.add(location);
      ArrayList<Location> openList = new ArrayList<>(connections);
      Location connection;
      while (openList.size() > 0){ //Add all its connections to the grid
        connection = openList.get(0);
        if(closedSet.contains(connection)) continue; //Skip if they are already in a grid

        //Add it to the grid
        HashSet<Location> connectionConnections = powerConnectors.get(connection);
        connectionConnections.remove(breakPoint);
        grid.powerConnectors.put(connection,connectionConnections);
        grid.blockConnections.put(connection, blockConnections.get(connection));

        //Continue traversal
        closedSet.add(connection);
        for(Location l: connectionConnections) {
          if(!closedSet.contains(l)) openList.add(l);
        }
      }
      managers.add(grid);
    }

    return managers;
  }

  public HashSet<Location> getCells() {
    return cells;
  }

  public HashSet<Location> getGenerators() {
    return generators;
  }

  public HashSet<Location> getMachines() {
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
    for (Location loc : cells) {
      BaseCell cell = new IronCell(); //TODO replace with get power block from location
      amount += cell.getEnergySpace();
    }
    return amount;
  }

  /* Calculates how much energy the generators produced this tick */
  private int calculateEnergyProduced(int limit) {
    int amount = 0;
    int e;
    for (Location loc : generators) {
      BaseGenerator generator = new SolidFuelGenerator(); //TODO replace with get power block from location
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
    for (Location loc : machines) {
      BaseMachine machine = new IronElectricFurnace(); //TODO replace with get power block from location
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
    for (Location loc : cells) {
      BaseCell cell = new IronCell(); //TODO replace with get power block from location
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
    for (Location loc : cells) {
      BaseCell cell = new IronCell(); //TODO replace with get power block from location
      amount -= cell.receiveEnergy(amount, false);
    }
  }

  /* Provides the machines with energy.
   * Used when there is enough for all the machines  */
  private int giveThePeopleWhatTheyWant(int amount) {
    for (Location loc : machines) {
      BaseMachine machine = new IronElectricFurnace(); //TODO replace with get power block from location
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
      for (Location loc : machines) {
        BaseMachine machine = new IronElectricFurnace(); //TODO replace with get power block from location
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
    cells = (HashSet<Location>) in.readObject();
    generators = (HashSet<Location>) in.readObject();
    machines = (HashSet<Location>) in.readObject();
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

  public void addPowerCell(Location connector, Location cellLocation) {
    cells.add(cellLocation);
    addBlockConnection(connector,cellLocation);
  }

  public void addMachine(Location connector, Location machineLocation) {
    machines.add(machineLocation);
    addBlockConnection(connector,machineLocation);
  }

  public void addGenerator(Location connector, Location generatorLocation) {
    generators.add(generatorLocation);
    addBlockConnection(connector,generatorLocation);
  }

  private void addBlockConnection(Location connector, Location machine) {
    HashSet<Location> temp = blockConnections.get(connector);
    temp.add(machine);
    blockConnections.replace(connector, temp);
  }
}
