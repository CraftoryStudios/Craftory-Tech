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
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerEntityDestroy;
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerSpawnEntityLiving;
import tech.brettsaunders.craftory.utils.Logger;

public class NewBeam {

  private static int magma = 44;

  public static WrapperPlayServerEntityDestroy spawnEntityForPlayer(Player player, Location location) {
    Logger.info("method top");
    WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
    packet.setX(location.getX());
    packet.setY(location.getY());
    packet.setZ(location.getZ());
    packet.setYaw(location.getYaw());
    packet.setPitch(location.getPitch());
    packet.setType(UpdatedEntityType.MAGMA_CUBE);
    packet.setUniqueId(UUID.randomUUID());
    packet.setEntityID(696969);
    Logger.info("Sending");
    packet.sendPacket(player);
    Logger.info("'Sent'");

    WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
    destroyPacket.setEntityIds(new int[] {696969});
    return destroyPacket;
  }

}
