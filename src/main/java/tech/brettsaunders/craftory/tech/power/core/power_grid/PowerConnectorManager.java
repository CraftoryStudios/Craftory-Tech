/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.power_grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.PoweredBlockUtils;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;
import tech.brettsaunders.craftory.tech.power.api.block.BaseGenerator;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.api.effect.Wire;
import tech.brettsaunders.craftory.utils.Log;

public class PowerConnectorManager implements Listener {

  private final HashMap<UUID, Location> formingConnection;
  private final HashMap<Location, ArrayList<Wire>> activeBeams;
  private final HashMap<Location, Location> beamLocations;
  private final HashSet<UUID> recentClicks;

  public PowerConnectorManager() {
    formingConnection = new HashMap<>();
    activeBeams = new HashMap<>();
    beamLocations = new HashMap<>();
    recentClicks = new HashSet<>();
  }

  @EventHandler
  public void useWrenchFormConnection(PlayerInteractEvent event) {
    //Check using wrench
    if (event.getHand() == EquipmentSlot.HAND && CustomItemManager.matchCustomItemName(event.getItem(), CoreHolder.Items.WRENCH)
        && event.getAction() == Action.RIGHT_CLICK_BLOCK && !recentlyClicked(event.getPlayer().getUniqueId())) {
      //Check Power Connector
      final Location location = event.getClickedBlock().getLocation();
      if (Craftory.customBlockManager.isCustomBlockOfType(location,
          CoreHolder.Blocks.POWER_CONNECTOR)) {
        connectorWrenchClick(location, event.getPlayer());
      } else if (
          PoweredBlockUtils.isPoweredBlock(location)
              && formingConnection.containsKey(event.getPlayer().getUniqueId())) {

        Location toLoc = location;
        Location fromLoc = formingConnection.get(event.getPlayer().getUniqueId());
        PowerGrid gridManager = Craftory.powerGridManager
            .getPowerGrid(fromLoc);
        PoweredBlock block = PoweredBlockUtils.getPoweredBlock(toLoc);
        if (block instanceof BaseMachine) {
          gridManager.addMachine(fromLoc, toLoc);
        } else if (block instanceof BaseGenerator) {
          gridManager.addGenerator(fromLoc, toLoc);
        } else if (block instanceof BaseCell) {
          gridManager.addPowerCell(fromLoc, toLoc);
        } else {
          event.getPlayer().sendMessage(Utilities.getTranslation("PowerConnectorBlockMismatch"));
          formingConnection.remove(event.getPlayer().getUniqueId());
          return;
        }
        formWire(fromLoc, toLoc);
        event.getPlayer().sendMessage(Utilities.getTranslation("PowerConnectorMachine"));
        formingConnection.remove(event.getPlayer().getUniqueId());
      }


    }
  }

  private void connectorWrenchClick(Location location, Player player) {
    UUID uuid = player.getUniqueId();
    if (!formingConnection.containsKey(uuid)) {
      //First Power Connector selected
      if (Craftory.powerGridManager.isPowerGrid(location)) {
        formingConnection.put(uuid, location);
        player.sendMessage(Utilities.getTranslation("PowerConnectorSecond"));
      }
    } else {
      //Locations
      Location toLoc = location;
      Location fromLoc = formingConnection.get(uuid);
      //Second Power Connector selected
      PowerGrid powerGridTo = Craftory.powerGridManager.getPowerGrid(toLoc);
      PowerGrid powerGridFrom = Craftory.powerGridManager.getPowerGrid(fromLoc);
      //Both have manager and not same power connector
      if (powerGridFrom != null && powerGridTo != null
          && !fromLoc.equals(toLoc)) {
        formConnection(toLoc, fromLoc, powerGridTo, powerGridFrom, player);
      } else {
        formingConnection.remove(uuid);
        player.sendMessage(Utilities.getTranslation("PowerConnectorFailed"));
        Log.debug((powerGridFrom == null) + "");
        Log.debug((powerGridTo == null) + "");
        Log.debug((fromLoc == toLoc) + "");
      }
    }
  }

  private void formConnection(Location toLoc, Location fromLoc, PowerGrid toGrid, PowerGrid fromGrid, Player player) {
    formingConnection.remove(player.getUniqueId());
    fromGrid.addPowerConnectorConnection(fromLoc, toLoc);
    //Merge Managers
    if (fromGrid != toGrid) {
      if (fromGrid.getGridSize() >= toGrid.getGridSize()) {
        fromGrid.addAll(toGrid);
        fromGrid.addPowerConnectorConnection(fromLoc, toLoc);
        Craftory.powerGridManager
            .mergeGrids(toGrid, fromGrid);
      } else {
        toGrid.addAll(fromGrid);
        toGrid.addPowerConnectorConnection(fromLoc, toLoc);
        Craftory.powerGridManager
            .mergeGrids(fromGrid, toGrid);
      }
    }
    formWire(fromLoc, toLoc);
    player.sendMessage(Utilities.getTranslation("PowerConnectorFormed"));
  }

  private boolean recentlyClicked(UUID uuid) {
    if(recentClicks.contains(uuid)){
      Log.debug("Duplicate interact event");
      return true;
    } else {
      recentClicks.add(uuid);
      Tasks.runTaskLater(() -> recentClicks.remove(uuid),4);
      return false;
    }
  }

  public void formWire(Location fromLoc, Location toLoc) {
    if(beamLocations.get(fromLoc)!=null && beamLocations.get(fromLoc).equals(toLoc)) return;
    Wire beam = new Wire(fromLoc.clone().add(0.5, 0.5, 0.5), toLoc.clone().add(0.5, 0.5, 0.5),
          -1, 25);
    beam.start(Craftory.plugin);
    addBeamToList(fromLoc, beam);
    addBeamToList(toLoc, beam);
    beamLocations.put(fromLoc, toLoc);
    beamLocations.put(toLoc, fromLoc);
  }

  private void addBeamToList(Location location, Wire beam) {
    ArrayList<Wire> temp;
    if (activeBeams.containsKey(location)) {
      temp = activeBeams.get(location);
      temp.add(beam);
      activeBeams.put(location, temp);
    } else {
      activeBeams.put(location, new ArrayList<>(Collections.singletonList(beam)));
    }
  }

  public void destroyBeams(Location loc) {
    if (activeBeams.containsKey(loc)) {
      activeBeams.get(loc).forEach(beam -> {
        if (activeBeams.containsKey(beam.getEnd())) {
          activeBeams.get(beam.getEnd()).forEach(Wire::stop);
        }
        beam.stop();
      });
      activeBeams.remove(loc);
    }
  }

  private void destroyActiveBeams() {
    for (ArrayList<Wire> list : activeBeams.values()) {
      list.forEach(Wire::stop);
    }
  }

  @EventHandler
  public void onDisable(PluginDisableEvent event) {
    destroyActiveBeams();
  }

}
