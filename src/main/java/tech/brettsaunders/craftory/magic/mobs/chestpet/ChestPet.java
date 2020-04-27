package tech.brettsaunders.craftory.magic.mobs.chestpet;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.HashMap;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.FollowTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChestPet implements Listener {

  HashMap<UUID, Inventory> chests = new HashMap<>();

  @EventHandler
  public void onPlayerIntereactEntity(PlayerInteractEntityEvent e) {
    UUID id = e.getRightClicked().getUniqueId();
    if (chests.containsKey(id)) {
      e.getPlayer().openInventory(chests.get(id));
    }
  }

  public void spawnChestPet(Player player, Location loc, ItemStack[] items) {
    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, "MARTY");
    npc.spawn(loc);
    npc.getTrait(ChestPetTrait.class).toggle(player, false);
    Zombie npcEntity = (Zombie) npc.getEntity();
    Inventory inventory = Bukkit.createInventory(null, 27);
    if (items != null) {
      inventory.setContents(items);
    }
    chests.put(npcEntity.getUniqueId(), inventory);
    npc.getTrait(ChestPetTrait.class).setInventory(inventory);
  }
}
