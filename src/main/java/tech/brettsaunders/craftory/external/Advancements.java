package tech.brettsaunders.craftory.external;

import eu.endercentral.crazy_advancements.Advancement;
import eu.endercentral.crazy_advancements.AdvancementDisplay;
import eu.endercentral.crazy_advancements.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.AdvancementVisibility;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class Advancements implements Listener {

  private AdvancementManager advancementManager;
  private AdvancementDisplay advancementDisplayCraftory;
  private Advancement steel_ingot;

  public Advancements() {
    if (Utilities.advancementManager.isPresent()) {
      Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
      advancementManager = Utilities.advancementManager.get();
      registerBase();
    }
  }

  private void registerBase() {

    advancementDisplayCraftory = new AdvancementDisplay(CustomItemManager.getCustomItem(Blocks.EMERALD_CELL), "Craftory", "r point for Craftory",
        AdvancementFrame.TASK, true, true, AdvancementVisibility.VANILLA);
    advancementDisplayCraftory.setBackgroundTexture("textures/block/iron_block.png");
    steel_ingot = new Advancement(null, new NameKey("custom","test"),advancementDisplayCraftory);
    steel_ingot.setCriteria(1);
    advancementManager.addAdvancement(steel_ingot);
  }

  @EventHandler
  public void addAdvancementsOnJoin(PlayerJoinEvent e) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Craftory.plugin, () -> {
      advancementManager.addPlayer(e.getPlayer());
      advancementManager.grantAdvancement(e.getPlayer().getUniqueId(), steel_ingot);
    }, 5);
  }

  @EventHandler
  public void removeAdvancementsOnQuit(PlayerQuitEvent e) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Craftory.plugin, () -> {
      advancementManager.removePlayer(e.getPlayer());
    }, 5);
  }
}
