/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.utils;

import org.bukkit.Location;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class Light {

  public static void deleteLight(Location location, boolean async) {
    LightAPI.deleteLight(location, async);
    for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, 15)) {
      LightAPI.updateChunk(chunkInfo, LightType.SKY);
     }
  }

  public static void createLight(Location location, LightType type, int level, boolean async) {
    LightAPI.createLight(location, type, level  , async);
     for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, type, level)) {
       LightAPI.updateChunk(chunkInfo, type);
    }
  }

  public static void createLight(Location location, int level, boolean async) {
    createLight(location, LightType.BLOCK, level, async);
  }
}
