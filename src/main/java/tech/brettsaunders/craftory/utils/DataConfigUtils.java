/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class DataConfigUtils {

  public static void copyDefaults(ConfigurationSection source, ConfigurationSection dest) {
    source.getValues(true).forEach((key,value) -> {
      dest.addDefault(key, value);
    });
  }

  /**
   * A method to serialize an inventory to Base64 string.
   *
   * <p />
   *
   * Special thanks to Comphenix in the Bukkit forums or also known
   * as aadnk on GitHub.
   *
   * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
   *
   * @param inventory to serialize
   * @return Base64 string of the provided inventory
   * @throws IllegalStateException
   */
  public static String toBase64(Inventory inventory) throws IllegalStateException {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

      // Write the size of the inventory
      dataOutput.writeInt(inventory.getSize());

      // Save every element in the list
      for (int i = 0; i < inventory.getSize(); i++) {
        dataOutput.writeObject(inventory.getItem(i));
      }

      // Serialize that array
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  /**
   *
   * A method to get an {@link Inventory} from an encoded, Base64, string.
   *
   * <p />
   *
   * Special thanks to Comphenix in the Bukkit forums or also known
   * as aadnk on GitHub.
   *
   * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
   *
   * @param data Base64 string of data containing an inventory.
   * @return Inventory created from the Base64 string.
   * @throws IOException
   */
  public static Inventory fromBase64(String data) throws IOException {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

      // Read the serialized inventory
      for (int i = 0; i < inventory.getSize(); i++) {
        inventory.setItem(i, (ItemStack) dataInput.readObject());
      }

      dataInput.close();
      return inventory;
    } catch (ClassNotFoundException e) {
      throw new IOException("Unable to decode class type.", e);
    }
  }
}
