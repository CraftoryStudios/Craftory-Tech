package tech.brettsaunders.craftory.magic.mobs.chestpet;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
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
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class ChestPet implements Listener {

  HashMap<Integer, Inventory> chests;
  private String SAVE_PATH = "ChestPet.data";

  public ChestPet(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    chests = new HashMap<>();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      ChestPetData data = (ChestPetData) in.readObject();
      chests = data.chests;
      in.close();
      Bukkit.getLogger().info("*** ChestPets Inventory Loaded");
    } catch (IOException e) {
      Bukkit.getLogger().info("*** New ChestPets Inventory Created");
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    } catch (Exception e) {
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    }
  }


  @EventHandler
  public void onPlayerIntereactEntity(PlayerInteractEntityEvent e) {
    if(!CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked())) return;
    Integer id = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked()).getId();
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
    chests.put(npc.getId(), inventory);
    npc.getTrait(ChestPetTrait.class).setInventory(inventory);
  }

  public void save() {
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));
      out.writeObject(chests);
      out.close();
      Bukkit.getLogger().info("Barrel Saved");
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().info("Barrel failed to save " + e);
    }
  }

  private static class ChestPetData implements Serializable {

    private static transient final long serialVersionUID = -1692222206529284441L;

    protected HashMap<Integer, Inventory> chests;

    public ChestPetData(HashMap<Integer, Inventory> chests) {
      this.chests = chests;
    }

  }
}
