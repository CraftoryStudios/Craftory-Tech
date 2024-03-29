/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.power_grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockUtils;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.utils.Log;

public class PowerGrid extends BukkitRunnable {

  @Getter
  @Setter
  private HashMap<Location, HashSet<Location>> powerConnectors =
      new HashMap<>();
  @Getter
  @Setter
  private HashMap<Location, HashSet<Location>> blockConnections = new HashMap<>();
  @Getter
  private HashSet<Location> cells = new HashSet<>();
  @Getter
  private HashSet<Location> generators = new HashSet<>();
  @Getter
  private HashSet<Location> machines = new HashSet<>();
  private int machinesNeedingEnergy = 0;
  private final BukkitTask taskID;

  public PowerGrid() {
    taskID = this.runTaskTimer(Craftory.plugin, 5, 1);
  }

  public void cancelTask() {
    this.cancel();
    taskID.cancel();
  }

  @Override
  public void run() {
    //Log.debug("grid run " + generators.size() + " " + machines.size() + " " + cells.size());//rr
    int needed = calculateGridEnergyRequirement();
    int cellCapacity = calculateGridStorageSpace();
    int produced = calculateGridEnergyProduced(needed + cellCapacity);
    if (needed > produced) {
      produced += retrieveGridEnergyStorage(needed - produced);
    }
    if (produced > needed) {
      int extra = distributeGridEnergy(produced);
      distributeExcessGridEnergy(extra);
    } else {
      distributeGridEnergyEvenly(produced);
    }
  }

  /* Calculate Grid Energy */

  /* Calculates how much energy the machines can take this tick */
  private int calculateGridEnergyRequirement() {
    machinesNeedingEnergy = 0;
    int amount = 0;
    int e;
    for (Iterator<Location> iter = machines.iterator(); iter.hasNext(); ) {
      Location location = iter.next();
      PoweredBlock poweredBlock = PoweredBlockUtils.getPoweredBlock(location);
      if (poweredBlock instanceof BaseMachine) {
        BaseMachine machine = (BaseMachine) poweredBlock;
        e = machine.getEnergySpace();
        if (e > 0) {
          amount += e;
          machinesNeedingEnergy += 1;
        }
      } else if (Objects.nonNull(poweredBlock)) {
        iter.remove();
        removeBlockConnection(location);
      }
    }
    return amount;
  }

  private int calculateGridStorageSpace() {
    int amount = 0;
    for(Iterator<Location> iter = cells.iterator(); iter.hasNext();) {
      Location location = iter.next();
      PoweredBlock poweredBlock = PoweredBlockUtils.getPoweredBlock(location);
      if(poweredBlock instanceof  BaseCell) {
        BaseCell cell = (BaseCell) poweredBlock;
        amount += cell.getEnergySpace();
      } else if (Objects.nonNull(poweredBlock)) {
        iter.remove();
        removeBlockConnection(location);
      }
    }
    return amount;
  }

  /* Calculates how much energy the generators produced this tick */
  private int calculateGridEnergyProduced(int limit) {
    int amount = 0;
    int energy;
    for(Iterator<Location> iter = generators.iterator(); iter.hasNext();) {
      Location location = iter.next();
      PoweredBlock poweredBlock = PoweredBlockUtils.getPoweredBlock(location);
      if(poweredBlock instanceof  BaseGenerator) {
        BaseGenerator generator = (BaseGenerator) poweredBlock;
        energy = generator.getEnergyAvailable();
        if (amount + energy > limit) {
          energy = limit - amount;
        }
        energy = generator.retrieveEnergy(energy);
        amount += energy;
        if (amount == limit) {
          break;
        }
      } else if (Objects.nonNull(poweredBlock)) {
        iter.remove();
        removeBlockConnection(location);
      }
    }
    return amount;
  }

  /* Retrieve Grid Energy */

  /**
   * Attempts to gather energy from storage
   *
   * @param goal amount of energy to gather
   * @return amount gathered
   */
  private int retrieveGridEnergyStorage(int goal) {
    int amount = 0;
    for (Location loc : cells) {
      BaseCell cell = (BaseCell) PoweredBlockUtils.getPoweredBlock(loc);
      if (Objects.nonNull(cell)) {
        amount += cell.retrieveEnergy((goal - amount));
        if (amount >= goal) {
          break;
        }
      }
    }
    return amount;
  }

  /* Distribute Grid Energy */

  /* Provides the machines with energy.
   * Used when there is enough for all the machines  */
  private int distributeGridEnergy(int amount) {
    for (Location loc : machines) {
      BaseMachine machine = (BaseMachine) PoweredBlockUtils.getPoweredBlock(loc);
      if (Objects.nonNull(machine)) {
        amount -= machine.receiveEnergy(amount, false);
      }
    }
    return amount;
  }

  /**
   * Puts excess energy into storage
   *
   * @param amount the amount of excess energy
   */
  private void distributeExcessGridEnergy(int amount) {
    for (Location loc : cells) {
      BaseCell cell = (BaseCell) PoweredBlockUtils.getPoweredBlock(loc);
      if (Objects.nonNull(cell)) {
        amount -= cell.receiveEnergy(amount, false);
      }
    }
  }

  /* Shares the available energy amongst the machines
   * Used when there is not enough for all machines  */
  private void distributeGridEnergyEvenly(int amount) {
    int allotment = amount;
    if (machinesNeedingEnergy > 1) {
      allotment = amount / machinesNeedingEnergy;
    }
    int c = 0;
    while (amount > 1 && c < 3) {
      c += 1;
      for (Location loc : machines) {
        BaseMachine machine = (BaseMachine) PoweredBlockUtils.getPoweredBlock(loc);
        if (Objects.nonNull(machine)) {
          amount -= machine.receiveEnergy(allotment, false);
        }
      }
    }
  }

  /* On Place */
  public void findPoweredBlocks() {
    cells = new HashSet<>();
    generators = new HashSet<>();
    machines = new HashSet<>();
    PoweredBlock block;
    Log.debug("grid has " + blockConnections.size() + " machine connections");
    for (HashSet<Location> set : blockConnections.values()) {
      for (Location location : set) {
        block = PoweredBlockUtils.getPoweredBlock(location);
        if (Objects.isNull(block)) { //Shouldn't be
          Log.debug("block in new grid gave null pointer");
        } else if (block instanceof BaseCell) {
          cells.add(location);
        } else if (block instanceof BaseGenerator) {
          generators.add(location);
        } else if (block instanceof BaseMachine) {
          machines.add(location);
        } else {
          Log.warn("Machine is not one of known types " + block.toString());
        }
      }
    }
  }

  public boolean removeCell(Location location) {
    return cells.remove(location);
  }

  public boolean removeMachine(Location location) {
    return machines.remove(location);
  }

  public boolean removeGenerator(Location location) {
    return generators.remove(location);
  }

  /* Getters & Setters */
  public int getGridSize() {
    return powerConnectors.size();
  }

  /* Common Methods */
  public void addPowerConnector(Location location) {
    this.powerConnectors.put(location, new HashSet<>());
  }

  public void addAll(PowerGrid other) {
    cells.addAll(other.getCells());
    generators.addAll(other.getGenerators());
    machines.addAll(other.getMachines());
    powerConnectors.putAll(other.powerConnectors);
    blockConnections.putAll(other.blockConnections);
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

  private boolean removeBlockConnection(Location machine) {
    for(Set<Location> locations : blockConnections.values()) {
      if(locations.remove(machine)) {
        Craftory.powerConnectorManager.destroyBeams(machine);
        return true;
      }
    }
    return false;
  }
}
