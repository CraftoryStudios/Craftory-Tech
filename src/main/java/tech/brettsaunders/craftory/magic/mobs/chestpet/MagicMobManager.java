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

public class MagicMobManager implements Listener {

  HashMap<UUID, Integer> mobData;
  private String SAVE_PATH = "MagicMobManager.data";

  public MagicMobManager(String folder) {
    SAVE_PATH = folder + File.separator + SAVE_PATH;
    mobData = new HashMap<>();
    try {
      BukkitObjectInputStream in = new BukkitObjectInputStream(
          new GZIPInputStream(new FileInputStream(SAVE_PATH)));
      MobDataManager data = (MobDataManager) in.readObject();
      mobData = data.mobDataManager;
      in.close();
      Bukkit.getLogger().info("*** Mobs Loaded");
    } catch (IOException e) {
      Bukkit.getLogger().info("*** New Mobs Data Created");
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    } catch (Exception e) {
      Bukkit.getLogger().info(e.toString());
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));
      out.writeObject(mobData);
      out.close();
      Bukkit.getLogger().info("Barrel Saved");
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().info("Barrel failed to save " + e);
    }
  }

  //Mob Spawners
  public boolean createChestPet(Player player, Location loc, ItemStack[] items) {
    if (mobData.containsKey(player.getUniqueId())) return false;
    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, "MARTY");
    npc.spawn(loc);
    npc.getTrait(ChestPetTrait.class).toggle(player, false);
    Zombie npcEntity = (Zombie) npc.getEntity();
    Inventory inventory = Bukkit.createInventory(null, 27);
    if (items != null) {
      inventory.setContents(items);
    }
    mobData.put(player.getUniqueId(), npc.getId());
    npc.getTrait(ChestPetTrait.class).setInventory(inventory);
    return true;
  }

  private static class MobDataManager implements Serializable {
    private static transient final long serialVersionUID = -1692222206529284441L;
    protected HashMap<UUID, Integer> mobDataManager;
    public MobDataManager(HashMap<UUID, Integer> mobDataManager) {
      this.mobDataManager = mobDataManager;
    }
  }
}
