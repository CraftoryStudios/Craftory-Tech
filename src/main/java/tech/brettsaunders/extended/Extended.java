package tech.brettsaunders.extended;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.HashSet;
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
  public static BiMutliHashMap beltManagers = new BiMutliHashMap();

  @Override
  public void onEnable() {
    plugin = this;
    // Plugin startup logic
    getLogger().info("Now loaded!");
    getServer().getPluginManager().registerEvents(new BeltEvents(), this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new EntitySerach(), 1L, 1L);
    CursedEarth cursedEarth = new CursedEarth();
    getServer().getPluginManager().registerEvents(cursedEarth, this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, cursedEarth, 80L, 80L);
  }

  @Override
  public void onDisable() {
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
