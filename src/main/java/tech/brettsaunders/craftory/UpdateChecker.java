/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

public class UpdateChecker {

  private final Craftory plugin;
  private final int resourceId;

  public UpdateChecker(Craftory plugin, int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
  }

  public void getVersion(final Consumer<String> consumer) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try (InputStream inputStream = new URL(
          "https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId)
          .openStream(); Scanner scanner = new Scanner(inputStream)) {
        if (scanner.hasNext()) {
          consumer.accept(scanner.next());
        }
      } catch (IOException exception) {
        this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
      }
    });
  }
}
