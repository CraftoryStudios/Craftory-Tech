/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils.profiles;

import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.utils.DataConfigUtils;

public class Profile {
  @Getter
  boolean firstLogin;
  @Getter
  Optional<Inventory> playerInventory;

  public Profile() {
    firstLogin = true;
    playerInventory = Optional.empty();
  }

  public Profile(ConfigurationSection configurationSection) {
    firstLogin = false;
    if (configurationSection.contains("inventory")) {
      try {
        Inventory inventory = DataConfigUtils.fromBase64(configurationSection.getString("inventory"));
        playerInventory = Optional.of(inventory);
      } catch (IOException e) {
        e.printStackTrace();
        playerInventory = Optional.empty();
      }
    } else {
      playerInventory = Optional.empty();
    }
  }

  public void saveProfile(ConfigurationSection configuration) {
    playerInventory.ifPresent(inventory -> {
      String inventoryData = DataConfigUtils.toBase64(inventory);
      configuration.set("inventory",inventoryData);
    });
  }


}
