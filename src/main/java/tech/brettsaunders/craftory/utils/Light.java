/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import org.bukkit.Location;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;
import tech.brettsaunders.craftory.Craftory;

public class Light {

  public static void deleteLight(Location location, boolean async) {
    if (!Craftory.isLightAPIEnabled) return;
    LightAPI.deleteLight(location, async);
    for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, 15)) {
      LightAPI.updateChunk(chunkInfo, LightType.SKY);
     }
  }

  public static void createLight(Location location, LightType type, int level, boolean async) {
    if (!Craftory.isLightAPIEnabled) return;
    LightAPI.createLight(location, type, level  , async);
     for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, type, level)) {
       LightAPI.updateChunk(chunkInfo, type);
    }
  }

  public static void createLight(Location location, int level, boolean async) {
    if (!Craftory.isLightAPIEnabled) return;
    createLight(location, LightType.BLOCK, level, async);
  }
}
