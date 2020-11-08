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

import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;

public class DataConfigUtils {

  private DataConfigUtils() {
    throw new IllegalStateException("Utils Class");
  }

  public static void copyDefaults(FileConfiguration source, FileConfiguration dest) {
    Set<String> sectionKeys = source.getKeys(false);

    sectionKeys.forEach(section -> {
      if (dest.contains(section)) {
        source.getConfigurationSection(section).getValues(false).forEach((key,value) -> dest.getConfigurationSection(section).addDefault(key, value));
      } else {
        dest.addDefault(section, source.getConfigurationSection(section));
      }
    });

    dest.options().copyDefaults(true);
  }
}
