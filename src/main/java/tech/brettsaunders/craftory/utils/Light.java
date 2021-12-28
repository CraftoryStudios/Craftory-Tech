/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import org.bukkit.Location;
import tech.brettsaunders.craftory.Craftory;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;


public class Light {

  public static void deleteLight(Location location) {
    if (!Craftory.isLightAPIEnabled) return;
    LightAPI.get().setLightLevel(location.getWorld().getName(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(), 0);
  }

  public static void createLight(Location location, int level) {
    if (!Craftory.isLightAPIEnabled) return;
    LightAPI.get().setLightLevel(location.getWorld().getName(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(), level);
  }

}
