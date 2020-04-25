package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.FollowTrait;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public final class Craftory extends JavaPlugin {

  public static Craftory plugin;
  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();
  FileConfiguration config = getConfig();
  private CursedEarth cursedEarth = null;
  private Barrel barrel = null;
  private ChestPet chestPet = null;
  @Override
  public void onEnable() {
    plugin = this;
    // Plugin startup logic
    getLogger().info("Now Loading!");
    resourceSetup();

    //Register

    //Magic Classes
    if (config.getBoolean("enableMagic")) {
      cursedEarth = new CursedEarth(getDataFolder().getPath());
      getServer().getPluginManager().registerEvents(cursedEarth, this);
      getServer().getScheduler().scheduleSyncRepeatingTask(this, cursedEarth, 800L, 80L);
      chestPet = new ChestPet();
      getServer().getPluginManager().registerEvents(chestPet, this);
      getServer().getPluginManager().registerEvents(new Magic(chestPet), this);
      barrel = new Barrel(getDataFolder().getPath());
      getServer().getPluginManager().registerEvents(barrel, this);

    }

    //Tech Classes
    if (config.getBoolean("enableTech")) {
      getServer().getPluginManager().registerEvents(new BeltEvents(), this);
      getServer().getPluginManager().registerEvents(new DebugEvents(), this);
      getServer().getScheduler().scheduleSyncRepeatingTask(this, new EntitySerach(), 1L, 1L);
    }

  }

  @Override
  public void onDisable() {
    //Save Data
    DataContainer.saveData(chunkKeys, beltManagers);
    if (config.getBoolean("enableMagic")) {
      cursedEarth.save();
      barrel.save();
    }
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("matty")) {
      chestPet.spawnChestPet((Player) sender, ((Player) sender).getLocation(), null);
    }
    if (command.getName().equals("setCursedSpreadRate")) {
      try {
        cursedEarth.setSpreadRate(Float.parseFloat(args[0]));
      } catch (Exception e) {

      }
    }
    return false;
  }

  public void resourceSetup() {
    //Load Data
    DataContainer data = DataContainer.loadData();
    if (data.chunkKeys != null) {
      chunkKeys = data.chunkKeys;
    }
    if (data.beltManagers != null) {
      beltManagers = data.beltManagers;
    }
    config.addDefault("enableMagic", true);
    config.addDefault("enableTech", true);
    config.options().copyDefaults(true);
    saveConfig();

    File items = new File(getDataFolder().getParentFile(), "ItemsAdder/data/");
    items.mkdirs();
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"), items);
  }

}
