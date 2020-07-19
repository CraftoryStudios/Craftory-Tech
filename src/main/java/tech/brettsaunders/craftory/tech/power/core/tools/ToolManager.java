package tech.brettsaunders.craftory.tech.power.core.tools;

import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class ToolManager implements Listener {

  private static final HashSet<Material> plants = new HashSet<>();
  static {
    plants.addAll(Tag.CROPS.getValues());
    plants.addAll(Tag.FLOWERS.getValues());
    plants.add(Material.FERN);
    plants.add(Material.LARGE_FERN);
    plants.add(Material.TALL_GRASS);
    plants.add(Material.GRASS);
  }

  public ToolManager() {
    Craftory.plugin.getServer().getPluginManager().registerEvents(this,Craftory.plugin);
  }

  @EventHandler
  public void onSickleUse(BlockBreakEvent event) {
    if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_WOOD)) {
      getPlantsInRange(event.getBlock().getLocation(),2);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_STONE)) {
      getPlantsInRange(event.getBlock().getLocation(),4);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_IRON)) {
      getPlantsInRange(event.getBlock().getLocation(),6);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_GOLD)) {
      getPlantsInRange(event.getBlock().getLocation(),12);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_STEEL)) {
      getPlantsInRange(event.getBlock().getLocation(),8);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_COPPER)) {
      getPlantsInRange(event.getBlock().getLocation(),6);
    } else if (CustomItemManager.matchCustomItemName(event.getPlayer().getInventory().getItemInMainHand(),
        Items.SICKLE_DIAMOND)) {
      getPlantsInRange(event.getBlock().getLocation(),10);
    }
  }

  private int getPlantsInRange(Location startPoint, int range) {
    int halfRange = range / 2;
    int amount = 0;
    for (int x = -halfRange; x <= halfRange; x++) {
      for (int z = -halfRange; z <= halfRange; z++) {
        Location location = startPoint.clone().add(x, 0, z);
        if (plants.contains(location.getBlock().getType())) {
          amount++;
          location.getBlock().breakNaturally();
        }
      }
    }
    return amount;
  }

}
