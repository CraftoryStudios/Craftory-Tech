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

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerLoadEvent;
import tech.brettsaunders.craftory.Craftory;

public class ProfileRepository implements Listener {
  private static FileConfiguration profilesConfig;
  private static HashMap<UUID, Profile> profiles = new HashMap<>();

  public ProfileRepository() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onServerLoaded(ServerLoadEvent e) {
    File configFile = new File(Craftory.plugin.getDataFolder() + File.separator + "data", "profiles.yml");
    profilesConfig = YamlConfiguration.loadConfiguration(configFile);

    profilesConfig.getKeys(false).forEach(key -> {
      ConfigurationSection configurationSection = profilesConfig.getConfigurationSection(key);
      profiles.put(UUID.fromString(key), new Profile(configurationSection));
    });
  }

  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    profiles.forEach((uuid, profile) -> saveProfile(uuid));
  }

  public static void saveProfile(UUID uuid) {
    if (profiles.containsKey(uuid)) {
      ConfigurationSection section = profilesConfig.createSection(uuid.toString());
      profiles.get(uuid).saveProfile(section);
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    if (!profiles.containsKey(e.getPlayer().getUniqueId())) {
      profiles.put(e.getPlayer().getUniqueId(), new Profile());
    }
  }

  public static Optional<Profile> getPlayerProfile(UUID id) {
    return Optional.of(profiles.get(id));
  }

}
