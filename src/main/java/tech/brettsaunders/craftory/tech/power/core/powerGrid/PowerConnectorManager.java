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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.Location;
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
import tech.brettsaunders.craftory.utils.Logger;

public class PowerConnectorManager implements Listener {

  private final transient Object2ObjectOpenHashMap<UUID, Location> formingConnection;
  private final transient Object2ObjectOpenHashMap<Location, ArrayList<Wire>> activeBeams;
  private final transient Object2ObjectOpenHashMap<Location, Location> beamLocations;
  private final transient ObjectOpenHashSet<UUID> recentClicks;

  public PowerConnectorManager() {
    formingConnection = new Object2ObjectOpenHashMap<>();
    activeBeams = new Object2ObjectOpenHashMap<>();
    beamLocations = new Object2ObjectOpenHashMap<>();
    recentClicks = new ObjectOpenHashSet<>();
  }

  @EventHandler
  public void useWrenchFormConnection(PlayerInteractEvent event) {
    if (event.getHand() == EquipmentSlot.OFF_HAND) return;
    //Check using wrench
    if (CustomItemManager.matchCustomItemName(event.getItem(), CoreHolder.Items.WRENCH)
        && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

      if(recentClicks.contains(event.getPlayer().getUniqueId())){
        Logger.debug("Duplicate interact event");
        return;
      } else {
        recentClicks.add(event.getPlayer().getUniqueId());
        Tasks.runTaskLater(() -> recentClicks.remove(event.getPlayer().getUniqueId()),4);
      }
      //Check Power Connector
      final Location location = event.getClickedBlock().getLocation();
      if (Craftory.customBlockManager.isCustomBlockOfType(location,
          CoreHolder.Blocks.POWER_CONNECTOR)) {
        if (!formingConnection.containsKey(event.getPlayer().getUniqueId())) {
          //First Power Connector selected
          if (Craftory.powerGridManager.getPowerGrid(location) == null) {
            return;
          }
          formingConnection
              .put(event.getPlayer().getUniqueId(), location);
          event.getPlayer().sendMessage(Utilities.getTranslation("PowerConnectorSecond"));
        } else {
          //Locations
          Location toLoc = location;
          Location fromLoc = formingConnection.get(event.getPlayer().getUniqueId());
          //Second Power Connector selected
          PowerGrid powerGridTo = Craftory.powerGridManager.getPowerGrid(toLoc);
          PowerGrid powerGridFrom = Craftory.powerGridManager.getPowerGrid(fromLoc);
          //Both have manager and not same power connector
          if (powerGridFrom != null && powerGridTo != null
              && !fromLoc.equals(toLoc)) {
            //Form Graphical Connection
            formingConnection.remove(event.getPlayer().getUniqueId());
            powerGridFrom.addPowerConnectorConnection(fromLoc, toLoc);
            //Merge Managers
            if (powerGridFrom != powerGridTo) {
              if (powerGridFrom.getGridSize() >= powerGridTo.getGridSize()) {
                powerGridFrom.addAll(powerGridTo);
                powerGridFrom.addPowerConnectorConnection(fromLoc, toLoc);
                Craftory.powerGridManager
                    .mergeGrids(powerGridTo, powerGridFrom);
              } else {
                powerGridTo.addAll(powerGridFrom);
                powerGridTo.addPowerConnectorConnection(fromLoc, toLoc);
                Craftory.powerGridManager
                    .mergeGrids(powerGridFrom, powerGridTo);
              }

            }
            formWire(fromLoc, toLoc);
            event.getPlayer().sendMessage(Utilities.getTranslation("PowerConnectorFormed"));
          } else {
            formingConnection.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(Utilities.getTranslation("PowerConnectorFailed"));
            Logger.debug((powerGridFrom == null) + "");
            Logger.debug((powerGridTo == null) + "");
            Logger.debug((fromLoc == toLoc) + "");
          }
        }
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
