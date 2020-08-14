/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.powerGrid;

import de.tr7zw.changeme.nbtapi.NBTFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockUtils;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.persistence.PersistenceStorage;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.utils.Logger;

public class PowerGridManager implements Listener {

  private final PersistenceStorage persistenceStorage;
  @Getter
  private final HashMap<Location, PowerGrid> powerGrids;
  private NBTFile nbtFile;
  private NBTFile nbtFileBackup;

  public PowerGridManager() {
    persistenceStorage = new PersistenceStorage();
    try {
      nbtFile = new NBTFile(
          new File(Craftory.plugin.getDataFolder() + File.separator + "data", "PoweredGrids.nbt"));
      nbtFileBackup = new NBTFile(
          new File(Craftory.plugin.getDataFolder() + File.separator + "data",
              "PoweredGridsBackup.nbt"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    powerGrids = new HashMap<>();
    Events.registerEvents(this);
  }

  private void generatorPowerBeams() {
    powerGrids.forEach(((location, powerGrid) -> {
      powerGrid.getPowerConnectors().forEach((from, value) -> {
        value.forEach((to) -> {
          Craftory.powerConnectorManager.formBeam(from, to);
        });
      });

      powerGrid.getBlockConnections().forEach((from, value) -> {
        value.forEach((to) -> {
          Craftory.powerConnectorManager.formBeam(from, to);
        });
      });
    }));
  }

  /* Events */
  @EventHandler
  public void onPoweredBlockBreak(CustomBlockBreakEvent event) {
    Location location = event.getLocation();
    if (powerGrids.containsKey(location)) { //GRID / Power connector stuff
      Craftory.powerConnectorManager.destroyBeams(location);
      PowerGrid grid = powerGrids.remove(location);
      grid.cancelTask();
      if (grid.getGridSize() > 1) {
        List<PowerGrid> newGrids = splitGrids(location, grid);
        for (Location l : grid.getPowerConnectors().keySet()) {
          PowerGrid g = powerGrids.remove(l);
          if (!g.isCancelled()) {
            g.cancelTask(); //Stop runable
          }
        }
        for (PowerGrid newGrid : newGrids) {
          for (Location loc : newGrid.getPowerConnectors().keySet()) {
            powerGrids.put(loc, newGrid);
          }
        }
      }

    }
    //Destroy any beams
    Craftory.powerConnectorManager.destroyBeams(location);
    if (event.getCustomBlock() instanceof BaseMachine) {
      for (PowerGrid grid : new HashSet<>(powerGrids.values())) {
        if (grid.removeMachine(location)) {
          break;
        }
      }
    } else if (event.getCustomBlock() instanceof BaseCell) {
      for (PowerGrid grid : new HashSet<>(powerGrids.values())) {
        if (grid.removeCell(location)) {
          break;
        }
      }
    } else if (event.getCustomBlock() instanceof BaseGenerator) {
      for (PowerGrid grid : new HashSet<>(powerGrids.values())) {
        if (grid.removeGenerator(location)) {
          break;
        }
      }
    }
  }

  public void onDisable() {
    nbtFileBackup.getKeys().forEach(key -> nbtFileBackup.removeKey(key));
    nbtFileBackup.mergeCompound(nbtFile);
    try {
      nbtFileBackup.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
    PowerGridSaver container = new PowerGridSaver(groupPowerGrids());
    nbtFile.getKeys().forEach(key -> nbtFile.removeKey(key));
    persistenceStorage.saveFields(container, nbtFile);
    try {
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onEnable() {
    PowerGridSaver container = new PowerGridSaver();
    persistenceStorage.loadFields(container, nbtFile);
    try {
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
    ungroupPowerGrids(container.data);
    generatorPowerBeams();
  }

  private HashMap<PowerGrid, HashSet<Location>> groupPowerGrids() {
    HashMap<PowerGrid, HashSet<Location>> grouped = new HashMap<>();
    powerGrids.forEach(((location, powerGrid) -> {
      if (grouped.containsKey(powerGrid)) {
        grouped.get(powerGrid).add(location);
      } else {
        HashSet<Location> locationHashSet = new HashSet<>();
        locationHashSet.add(location);
        grouped.put(powerGrid, locationHashSet);
      }
    }));
    return grouped;
  }

  private void ungroupPowerGrids(HashMap<PowerGrid, HashSet<Location>> data) {
    data.forEach((powerGrid, locations) -> locations
        .forEach(location -> powerGrids.put(location, powerGrid)));
  }

  /* Grid Splitting */
  public void mergeGrids(PowerGrid old, PowerGrid merged) {
    for (HashMap.Entry<Location, PowerGrid> entry : powerGrids.entrySet()) {
      if (entry.getValue().equals(old)) {
        powerGrids.put(entry.getKey(), merged);
      }
    }
  }

  public void getAdjacentPowerBlocks(Location location, PowerGrid powerGrid) {
    Location blockLocation;
    for (BlockFace face : Utilities.faces) {
      blockLocation = location.getBlock().getRelative(face).getLocation();
      if (PoweredBlockUtils.isPoweredBlock(blockLocation)) {
        PoweredBlock poweredBlock = PoweredBlockUtils.getPoweredBlock(blockLocation);
        if (PoweredBlockUtils.isCell(poweredBlock)) {
          powerGrid.addPowerCell(location, blockLocation);
        } else if (PoweredBlockUtils.isGenerator(poweredBlock)) {
          powerGrid.addGenerator(location, blockLocation);
        } else if (PoweredBlockUtils.isMachine(poweredBlock)) {
          powerGrid.addMachine(location, blockLocation);
        }
      }
    }
  }

  public void addPowerGrid(Location location, PowerGrid manger) {
    powerGrids.put(location, manger);
  }

  public PowerGrid getPowerGrid(Location location) {
    return powerGrids.get(location);
  }

  public Boolean isPowerGrid(Location location) {
    return powerGrids.containsKey(location);
  }

  /**
   * Splits a power grid into the needed amount of new grids based on the power connector that broke
   * the grid
   *
   * @param breakPoint The location of the broken power connector
   * @return A list of the individual grids (could just be one)
   */
  public ArrayList<PowerGrid> splitGrids(Location breakPoint, PowerGrid powerGrid) {
    ArrayList<PowerGrid> managers = new ArrayList<>();
    powerGrid.getBlockConnections().remove(breakPoint);
    HashSet<Location> neighbours = powerGrid.getPowerConnectors().remove(breakPoint);
    Logger.debug("Connector had: " + neighbours.size());
    HashSet<Location> closedSet = new HashSet<>();
    neighbours.forEach(location -> { //Loop through all the neighbours of broken connector
      if (!closedSet.contains(location)) {
        closedSet.add(location);
        PowerGrid grid = new PowerGrid();
        HashSet<Location> connections = powerGrid.getPowerConnectors().get(location);
        if (connections != null) {
          connections.remove(breakPoint);
          grid.getPowerConnectors().put(location, connections);
          if (powerGrid.getBlockConnections().containsKey(location)) {
            grid.getBlockConnections().put(location, powerGrid.getBlockConnections().get(location));
          }
          ArrayList<Location> openList = new ArrayList<>(connections);
          Location connection;
          while (openList.size() > 0) { //Add all its connections to the grid
            connection = openList.remove(0);
            if (closedSet.contains(connection)) {
              continue; //Skip if they are already in a grid
            }
            closedSet.add(connection);
            //Add it to the grid
            if (powerGrid.getBlockConnections().containsKey(connection)) {
              grid.getBlockConnections()
                  .put(connection, powerGrid.getBlockConnections().get(connection));
            }
            HashSet<Location> connectionConnections = powerGrid.getPowerConnectors()
                .get(connection);
            if (connectionConnections == null) {
              continue;
            }
            connectionConnections.remove(breakPoint);
            grid.getPowerConnectors().put(connection, connectionConnections);
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


}
