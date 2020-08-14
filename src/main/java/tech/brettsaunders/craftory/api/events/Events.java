/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.events;

import org.bukkit.event.Listener;
import tech.brettsaunders.craftory.Craftory;

public class Events {

  public static void registerEvents(Listener listener) {
    Craftory.plugin.getServer().getPluginManager().registerEvents(listener, Craftory.plugin);
  }

}
