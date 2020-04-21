package tech.brettsaunders.extended;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.crypto.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public final class Extended extends JavaPlugin {

  public static Extended plugin;
  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();

  @Override
  public void onEnable() {
    plugin = this;
    // Plugin startup logic
    getLogger().info("Now Loading!");
    //Load Data
    DataContainer data = DataContainer.loadData();
    chunkKeys = data.chunkKeys;
    beltManagers = data.beltManagers;
    //Register
    getServer().getPluginManager().registerEvents(new BeltEvents(), this);
    getServer().getPluginManager().registerEvents(new DebugEvents(), this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new EntitySerach(), 1L, 1L);
    CursedEarth cursedEarth = new CursedEarth();
    getServer().getPluginManager().registerEvents(cursedEarth, this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, cursedEarth, 80L, 80L);
  }

  @Override
  public void onDisable() {
    //Save Data
    DataContainer.saveData(chunkKeys, beltManagers);
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender,  Command command,  String label, String[] args) {
    if(command.getName().equals("matty")) {
      Player player = (Player) sender;
      Block block = player.getLocation().add(0,0,-2).getBlock();
      block.setType(Material.STONE);
    }
    return false;
  }



}
