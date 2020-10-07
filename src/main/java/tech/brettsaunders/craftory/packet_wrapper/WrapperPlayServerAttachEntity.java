/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.packet_wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerAttachEntity extends AbstractPacket {
  public static final PacketType TYPE = PacketType.Play.Server.ATTACH_ENTITY;

  public WrapperPlayServerAttachEntity() {
    super(new PacketContainer(TYPE), TYPE);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerAttachEntity(PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Entity1 ID.
   * <p>
   * Notes: entity's ID
   *
   * @return The current Entity ID
   */
  public int getEntity1ID() {
    return handle.getIntegers().read(0);
  }

  /**
   * Set Entity1 ID.
   *
   * @param value - new value.
   */
  public void setEntity1ID(int value) {
    handle.getIntegers().write(0, value);
  }


  /**
   * Retrieve Entity2 ID.
   * <p>
   * Notes: entity's ID
   *
   * @return The current Entity ID
   */
  public int getEntity2ID() {
    return handle.getIntegers().read(1);
  }

  /**
   * Set Entity2 ID.
   *
   * @param value - new value.
   */
  public void setEntity2ID(int value) {
    handle.getIntegers().write(1, value);
  }
}