package tech.brettsaunders.craftory.magic.mobs.chestpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import tech.brettsaunders.craftory.utils.Logger;

public class MagicMobManager implements Listener {

  HashMap<String, String> mobData;
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
      Logger.info("Magic MobManager Loaded");
    } catch (IOException | ClassNotFoundException e) {
      Logger.info("Created new Magic MobManager Data");
      Logger.debug(e.toString());
    }
  }

  public void save() {
    try {
      MobDataManager data = new MobDataManager(mobData);
      BukkitObjectOutputStream out = new BukkitObjectOutputStream(
          new GZIPOutputStream(new FileOutputStream(SAVE_PATH)));

      out.writeObject(data);
      out.close();
      Logger.info("Magic MobManager Saved");
    } catch (IOException e) {
      Logger.warn("Failed to load Magic MobManager");
      Logger.debug(e.toString());
    }
  }

  //Mob Spawners
  public boolean createChestPet(Player player, Location loc, ItemStack[] items) {
    if (mobData.containsKey(player.getUniqueId().toString())) return false;
    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, "MARTY");
    npc.spawn(loc);
    npc.getTrait(ChestPetTrait.class).toggle(player, false);
    Inventory inventory = Bukkit.createInventory(null, 27);
    if (items != null) {
      inventory.setContents(items);
    }
    mobData.put(player.getUniqueId().toString(), npc.getUniqueId().toString());
    npc.getTrait(ChestPetTrait.class).setInventory(inventory);
    return true;
  }

  private static class MobDataManager implements Serializable {
    private static transient final long serialVersionUID = -1692222206529284441L;
    protected HashMap<String, String> mobDataManager;
    public MobDataManager(HashMap<String, String> mobDataManager) {
      this.mobDataManager = mobDataManager;
    }
  }
}
