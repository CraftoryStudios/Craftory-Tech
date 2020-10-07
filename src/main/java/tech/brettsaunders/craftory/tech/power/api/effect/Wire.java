/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.tech.power.api.effect;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Constants;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.packet_wrapper.AbstractPacket;
import tech.brettsaunders.craftory.packet_wrapper.UpdatedEntityType;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerAttachEntity;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerEntityDestroy;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerEntityMetadata;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerSpawnEntityLiving;


public class Wire {

  private final int duration;
  private final int distanceSquared;
  private final AbstractPacket[] createPackets;
  private final WrapperPlayServerEntityDestroy destroyPacket;
  private final HashSet<Player> show = new HashSet<>();
  private Location start;
  private Location end;
  private BukkitRunnable run;

  /**
   * Create a Beam instance
   *
   * @param start Location where Beam will starts
   * @param end Location where Beam will ends
   * @param duration Duration of Beam in seconds (<i>-1 if infinite</i>)
   * @param distance Distance where Beam will be visible
   */
  public Wire(Location start, Location end, int duration, int distance) {
    this.start = start;
    this.end = end;
    this.duration = duration;
    distanceSquared = distance * distance;
    AbstractPacket[] packets = WirePackets.createWirePackets(start, end);
    createPackets = Arrays.copyOfRange(packets,0,5);
    destroyPacket = (WrapperPlayServerEntityDestroy) packets[5];
  }

  public void start(Plugin plugin) {
    Validate.isTrue(run == null, "Task already started");
    run = new BukkitRunnable() {
      int time = duration;

      @Override
      public void run() {
          if (time == 0) {
            cancel();
            return;
          }
          for (Player p : start.getWorld().getPlayers()) {
            if (isCloseEnough(p.getLocation(), p)) {
              if (!show.contains(p)) {
                for(AbstractPacket packet: createPackets) {
                  packet.sendPacket(p);
                }
                show.add(p);
              }
            } else if (show.contains(p)) {
              destroyPacket.sendPacket(p);
              show.remove(p);
            }
          }
          if (time != -1) {
            time--;
          }
      }

      @Override
      public synchronized void cancel() {
        super.cancel();
        for (Player p : show) {
          destroyPacket.sendPacket(p);
        }
        run = null;
      }
    };
    run.runTaskTimerAsynchronously(plugin, 0L, 20L);
  }

  public void stop() {
    if (run != null) {
      run.cancel();
    }
  }

  public Location getEnd() {
    return end;
  }


  private boolean isCloseEnough(Location location, Player player) {
    if (CustomItemManager
        .matchCustomItemName(player.getInventory().getItemInMainHand(), Constants.Items.WRENCH) ||
        CustomItemManager.matchCustomItemName(player.getInventory().getItemInMainHand(),
            Constants.Blocks.POWER_CONNECTOR)) {
      return start.distanceSquared(location) <= distanceSquared ||
          end.distanceSquared(location) <= distanceSquared;
    } else {
      return false;
    }
  }


  public static WrapperPlayServerEntityDestroy spawnWire(Player player, Location location1, Location location2) {
    WrapperPlayServerSpawnEntityLiving packet1 = new WrapperPlayServerSpawnEntityLiving();
    packet1.setX(location1.getX());
    packet1.setY(location1.getY());
    packet1.setZ(location1.getZ());
    packet1.setYaw(location1.getYaw());
    packet1.setPitch(location1.getPitch());
    packet1.setType(UpdatedEntityType.COD);
    packet1.setUniqueId(UUID.randomUUID());
    packet1.setEntityID(10000);
    packet1.sendPacket(player);

    WrapperPlayServerSpawnEntityLiving packet2 = new WrapperPlayServerSpawnEntityLiving();
    packet2.setX(location2.getX());
    packet2.setY(location2.getY());
    packet2.setZ(location2.getZ());
    packet2.setYaw(location2.getYaw());
    packet2.setPitch(location2.getPitch());
    packet2.setType(UpdatedEntityType.COD);
    packet2.setUniqueId(UUID.randomUUID());
    packet2.setEntityID(10001);
    packet2.sendPacket(player);

    WrapperPlayServerAttachEntity packet3 = new WrapperPlayServerAttachEntity();
    packet3.setEntity1ID(10000);
    packet3.setEntity2ID(10001);
    packet3.sendPacket(player);

    WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
    destroyPacket.setEntityIds(new int[] {10000,10001});
    return destroyPacket;
  }

  public static class WirePackets {

    private WirePackets() {
      throw new IllegalStateException("Utils Class");
    }
    private static int lastIssuedEID = 2000000000;

    static int generateEID() {
      return lastIssuedEID++;
    }

    public static AbstractPacket[] createWirePackets(Location location1, Location location2) {
      AbstractPacket[] packets = new AbstractPacket[6];

      int id1 = generateEID();
      int id2 = generateEID();

      packets[0] = createSpawnPacket(location1, id1);
      packets[1] = createMetaPacket(id1);

      packets[2] = createSpawnPacket(location2, id2);
      packets[3] = createMetaPacket(id2);

      WrapperPlayServerAttachEntity leashPacket = new WrapperPlayServerAttachEntity();
      leashPacket.setEntity1ID(id1);
      leashPacket.setEntity2ID(id2);
      packets[4] = leashPacket;

      WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
      destroyPacket.setEntityIds(new int[] {id1,id2});
      packets[5] = destroyPacket;

      return packets;
    }

    private static WrapperPlayServerSpawnEntityLiving createSpawnPacket(Location location, int id) {
      WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();
      spawnPacket.setX(location.getX());
      spawnPacket.setY(location.getY());
      spawnPacket.setZ(location.getZ());
      spawnPacket.setYaw(location.getYaw());
      spawnPacket.setPitch(location.getPitch());
      spawnPacket.setType(UpdatedEntityType.COD);
      spawnPacket.setUniqueId(UUID.randomUUID());
      spawnPacket.setEntityID(id);
      return spawnPacket;
    }

    private static WrapperPlayServerEntityMetadata createMetaPacket(int id) {
      WrappedDataWatcher meta = new WrappedDataWatcher();
      meta.setObject(new WrappedDataWatcherObject(4, Registry.get(Boolean.class)), true);
      meta.setObject(new WrappedDataWatcherObject(5, Registry.get(Boolean.class)), true);
      meta.setObject(new WrappedDataWatcherObject(0, Registry.get(Byte.class)), (byte) 0x20);
      meta.setObject(new WrappedDataWatcherObject(14, Registry.get(Byte.class)), (byte) 0x10);

      WrapperPlayServerEntityMetadata metaPacket = new WrapperPlayServerEntityMetadata();
      metaPacket.setEntityID(id);
      metaPacket.setMetadata(meta.getWatchableObjects());
      return metaPacket;
    }

  }
}
