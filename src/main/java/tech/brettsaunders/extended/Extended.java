package tech.brettsaunders.extended;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public final class Extended extends JavaPlugin {

  public static Extended plugin;

  @Override
  public void onEnable() {
    plugin = this;
    // Plugin startup logic
    getLogger().info("Now loaded!");
    getServer().getPluginManager().registerEvents(new BeltEvents(), this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new EntitySerach(), 1L, 20L);
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
