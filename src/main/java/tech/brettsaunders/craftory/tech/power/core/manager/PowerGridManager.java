package tech.brettsaunders.craftory.tech.power.core.manager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerGridManager extends BukkitRunnable implements Externalizable {

  private static final long serialVersionUID = 10021L;
  public final HashMap<Location, HashSet<Location>> powerConnectors = new HashMap<>();
  protected final HashMap<Location, HashSet<Location>> blockConnections = new HashMap<>();
  private HashSet<Location> cells = new HashSet<>();
  private HashSet<Location> generators = new HashSet<>();
  private HashSet<Location> machines = new HashSet<>();
  private int machinesNeedingEnergy = 0;

  public PowerGridManager(Location powerConnector) {
    addPowerConnector(powerConnector);
    Bukkit.getScheduler().runTaskTimer(Craftory.plugin, this, 10, 1);
  }

  public PowerGridManager() {
    Bukkit.getScheduler().runTaskTimer(Craftory.plugin, this, 10, 1);
  }

  /**
   * Splits a power grid into the needed amount of new grids based on the power connector that broke
   * the grid
   *
   * @param breakPoint The location of the broken power connector
   * @return A list of the individual grids (could just be one)
   */
  public ArrayList<PowerGridManager> splitGrids(Location breakPoint) {
    ArrayList<PowerGridManager> managers = new ArrayList<>();
    blockConnections.remove(breakPoint);
    HashSet<Location> neighbours = powerConnectors.remove(breakPoint);
    Logger.info("connector had: " + neighbours.size());
    HashSet<Location> closedSet = new HashSet<>();
    for (Location location : neighbours) { //Loop through all the neighbours of broken connector

    }
    neighbours.forEach(location -> {
      if (!closedSet.contains(location)) {
        closedSet.add(location);
        Logger.info("making new grid");
        PowerGridManager grid = new PowerGridManager();
        HashSet<Location> connections = powerConnectors.get(location);
        if (connections != null) {
          connections.remove(breakPoint);
          grid.powerConnectors.put(location, connections);
          grid.blockConnections.put(location, blockConnections.get(location));
          ArrayList<Location> openList = new ArrayList<>(connections);
          Location connection;
          while (openList.size() > 0) { //Add all its connections to the grid
            connection = openList.remove(0);
            if (closedSet.contains(connection)) {
              continue; //Skip if they are already in a grid
            }
            closedSet.add(connection);
            //Add it to the grid
            if (blockConnections.containsKey(connection)) {
              grid.blockConnections.put(connection, blockConnections.get(connection));
            }
            HashSet<Location> connectionConnections = powerConnectors.get(connection);
            if (connectionConnections == null) {
              continue;
            }
            connectionConnections.remove(breakPoint);
            grid.powerConnectors.put(connection, connectionConnections);
            //Continue traversal
            connectionConnections.forEach(loc -> {
              if (!closedSet.contains(loc)) {
                openList.add(loc);
              }
            });
          }
        }
        grid.findPoweredBlocks();
        managers.add(grid);
      }

    });
    return managers;
  }

  private void findPoweredBlocks() {
    cells = new HashSet<>();
    generators = new HashSet<>();
    machines = new HashSet<>();
    PoweredBlock block;
    Logger.info("grid has " + blockConnections.size() + " machine connections");
    for (HashSet<Location> set : blockConnections.values()) {
      if (set == null) {
        continue;
      }
      for (Location location : set) {
        if (location == null) {
          continue;
        }
        block = Craftory.getBlockPoweredManager().getPoweredBlock(location);
        if (block instanceof BaseCell) {
          cells.add(location);
        } else if (block instanceof BaseGenerator) {
          generators.add(location);
        } else if (block instanceof BaseMachine) {
          machines.add(location);
        } else {
          Logger.info("Machine is not one of known types");
        }
      }
    }
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
    return powerConnectors.size();
  }

  private int calculateStorageSpace() {
    int amount = 0;
    HashSet<Location> toRemove = new HashSet<>();
    for (Location loc : cells) {
      BaseCell cell = (BaseCell) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
      if (cell == null) {
        toRemove.add(loc);
        continue;
      }
      amount += cell.getEnergySpace();
    }
    cells.removeAll(toRemove);
    return amount;
  }

  /* Calculates how much energy the generators produced this tick */
  private int calculateEnergyProduced(int limit) {
    int amount = 0;
    int e;
    HashSet<Location> toRemove = new HashSet<>();
    for (Location loc : generators) {
      BaseGenerator generator = (BaseGenerator) Craftory.getBlockPoweredManager()
          .getPoweredBlock(loc);
      if (generator == null) {
        toRemove.add(loc);
        continue;
      }
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
    generators.removeAll(toRemove);
    return amount;
  }

  /* Calculates how much energy the machines can take this tick */
  private int whatDoTheyNeed() {
    machinesNeedingEnergy = 0;
    int amount = 0;
    int e;
    HashSet<Location> toRemove = new HashSet<>();
    for (Location loc : machines) {
      BaseMachine machine = (BaseMachine) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
      if (machine == null) {
        toRemove.add(loc);
        continue;
      }
      e = machine.getEnergySpace();
      if (e > 0) {
        amount += e;
        machinesNeedingEnergy += 1;
      }
    }
    machines.removeAll(toRemove);
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
      BaseCell cell = (BaseCell) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
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
      BaseCell cell = (BaseCell) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
      amount -= cell.receiveEnergy(amount, false);
    }
  }

  /* Provides the machines with energy.
   * Used when there is enough for all the machines  */
  private int giveThePeopleWhatTheyWant(int amount) {
    for (Location loc : machines) {
      BaseMachine machine = (BaseMachine) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
      amount -= machine.receiveEnergy(amount, false);
    }
    return amount;
  }

  /* Shares the available energy amongst the machines
   * Used when there is not enough for all machines  */
  private void shareThisAmongstThePeople(int amount) {
    int allotment = amount;
    if (machinesNeedingEnergy > 1) {
      allotment = amount / machinesNeedingEnergy;
    }
    int c = 0;
    while (amount > 1 && c < 3) {
      c += 1;
      for (Location loc : machines) {
        BaseMachine machine = (BaseMachine) Craftory.getBlockPoweredManager().getPoweredBlock(loc);
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

  public void combineGrid(PowerGridManager other) {
    cells.addAll(other.getCells());
    generators.addAll(other.getGenerators());
    machines.addAll(other.getMachines());
    powerConnectors.putAll(other.powerConnectors);
    blockConnections.putAll(other.blockConnections);
  }


  /* Common Methods */
  public void addPowerConnector(Location location) {
    this.powerConnectors.put(location, new HashSet<>());
  }

  public void addPowerConnectorConnection(Location from, Location to) {
    HashSet<Location> temp = powerConnectors.get(from);
    if (temp == null) {
      temp = new HashSet<>();
    }
    temp.add(to);
    powerConnectors.put(from, temp);
    temp = powerConnectors.get(to);
    if (temp == null) {
      temp = new HashSet<>();
    }
    temp.add(from);
    powerConnectors.put(to, temp);
  }

  public void addPowerCell(Location connector, Location cellLocation) {
    cells.add(cellLocation);
    addBlockConnection(connector, cellLocation);
  }

  public void addMachine(Location connector, Location machineLocation) {
    machines.add(machineLocation);
    addBlockConnection(connector, machineLocation);
  }

  public void addGenerator(Location connector, Location generatorLocation) {
    generators.add(generatorLocation);
    addBlockConnection(connector, generatorLocation);
  }

  private void addBlockConnection(Location connector, Location machine) {
    HashSet<Location> temp = blockConnections.get(connector);
    if (temp == null) {
      temp = new HashSet<>();
    }
    temp.add(machine);
    blockConnections.put(connector, temp);
  }

  @Override
  public void run() {
    //Logger.info(cells.size() + " " + generators.size() + " " + machines.size());
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
}
