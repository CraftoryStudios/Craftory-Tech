package tech.brettsaunders.craftory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class Craftory extends JavaPlugin {

  public static Craftory plugin;
  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();
  public CursedEarth cursedEarth = new CursedEarth();
  FileConfiguration config = getConfig();

  @Override
  public void onEnable() {
    plugin = this;
    // Plugin startup logic
    getLogger().info("Now Loading!");
    resourceSetup();

    //Register

    //Magic Classes
    if (config.getBoolean("enableMagic")) {
      getServer().getPluginManager().registerEvents(cursedEarth, this);
      getServer().getScheduler().scheduleSyncRepeatingTask(this, cursedEarth, 80L, 80L);
      getServer().getPluginManager().registerEvents(new Magic(), this);
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
    DataContainer.saveData(chunkKeys, beltManagers, cursedEarth.getEarths(), cursedEarth.getClosedList());
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("matty")) {
      Player player = (Player) sender;
      Block block = player.getLocation().add(0, 0, -2).getBlock();
      block.setType(Material.STONE);
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
    if (data.earths != null){
      cursedEarth.setEarths(data.earths);
    }
    if (data.closedList != null){
      cursedEarth.setClosedList(data.closedList);
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
