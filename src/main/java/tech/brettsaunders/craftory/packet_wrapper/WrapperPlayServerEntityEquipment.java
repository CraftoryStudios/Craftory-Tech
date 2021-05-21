package tech.brettsaunders.craftory.packet_wrapper;

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {
  public static final PacketType TYPE =
      PacketType.Play.Server.ENTITY_EQUIPMENT;

  public WrapperPlayServerEntityEquipment() {
    super(new PacketContainer(TYPE), TYPE);
    handle.getModifier().writeDefaults();
  }

  /**
   * Set Entity ID.
   *
   * @param value - new value.
   */
  public void setEntityID(int value) {
    handle.getIntegers().write(0, value);
  }

  public void setSlot(ItemSlot value) {
    handle.getItemSlots().write(0, value);
  }

  /**
   * Set a ItemSlot - ItemStack pair.
   * @param slot The slot the item will be equipped in. If matches an existing pair, will overwrite the old one
   * @param item The item to equip
   */
  public void setSlotStackPair(ItemSlot slot, ItemStack item) {
    List<Pair<ItemSlot, ItemStack>> slotStackPairs = handle.getSlotStackPairLists().read(0);
    slotStackPairs.removeIf(pair -> pair.getFirst().equals(slot));
    slotStackPairs.add(new Pair<>(slot, item));
    handle.getSlotStackPairLists().write(0, slotStackPairs);
  }

  /**
   * Set Item.
   *
   * @param value - new value.
   */
  public void setItem(ItemStack value) {
    handle.getItemModifier().write(0, value);
  }
}
