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

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.PacketWrapper.UpdatedEntityType;
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerAttachEntity;
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerEntityDestroy;
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerSpawnEntityLiving;

public class NewBeam {

  public static WrapperPlayServerEntityDestroy spawnBeam(Player player, Location location1, Location location2) {
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

}
