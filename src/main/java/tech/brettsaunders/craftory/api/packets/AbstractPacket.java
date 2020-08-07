/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/
package tech.brettsaunders.craftory.api.packets;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket {
  // The packet we will be modifying
  protected PacketContainer handle;

  /**
   * Constructs a new strongly typed wrapper for the given packet.
   *
   * @param handle - handle to the raw packet data.
   * @param type - the packet type.
   */
  protected AbstractPacket(PacketContainer handle, PacketType type) {
    // Make sure we're given a valid packet
    if (handle == null)
      throw new IllegalArgumentException("Packet handle cannot be NULL.");
    if (!Objects.equal(handle.getType(), type))
      throw new IllegalArgumentException(handle.getHandle()
          + " is not a packet of type " + type);

    this.handle = handle;
  }

  /**
   * Retrieve a handle to the raw packet data.
   *
   * @return Raw packet data.
   */
  public PacketContainer getHandle() {
    return handle;
  }

  /**
   * Send the current packet to the given receiver.
   *
   * @param receiver - the receiver.
   * @throws RuntimeException If the packet cannot be sent.
   */
  public void sendPacket(Player receiver) {
    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(receiver,
          getHandle());
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Cannot send packet.", e);
    }
  }

  /**
   * Send the current packet to all online players.
   */
  public void broadcastPacket() {
    ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
  }

  /**
   * Simulate receiving the current packet from the given sender.
   *
   * @param sender - the sender.
   * @throws RuntimeException If the packet cannot be received.
   * @deprecated Misspelled. recieve to receive
   * @see #receivePacket(Player)
   */
  @Deprecated
  public void recievePacket(Player sender) {
    try {
      ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
          getHandle());
    } catch (Exception e) {
      throw new RuntimeException("Cannot recieve packet.", e);
    }
  }

  /**
   * Simulate receiving the current packet from the given sender.
   *
   * @param sender - the sender.
   * @throws RuntimeException if the packet cannot be received.
   */
  public void receivePacket(Player sender) {
    try {
      ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
          getHandle());
    } catch (Exception e) {
      throw new RuntimeException("Cannot receive packet.", e);
    }
  }
}