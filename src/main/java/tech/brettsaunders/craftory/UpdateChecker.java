/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
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
