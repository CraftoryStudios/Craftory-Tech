package tech.brettsaunders.craftory;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.DataConverterRegistry;
import net.minecraft.server.v1_15_R1.DataConverterTypes;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EnumCreatureType;
import net.minecraft.server.v1_15_R1.IRegistry;
import net.minecraft.server.v1_15_R1.SharedConstants;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;


public final class Craftory extends JavaPlugin {

  public static Craftory plugin;
  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();
  public CursedEarth cursedEarth = null;
  FileConfiguration config = getConfig();

  private static EntityTypes entityTypes;

  public static void registerEntity() {
    Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
        .findChoiceType(DataConverterTypes.ENTITY_TREE).types();
    types.put("minecraft:chestpet", types.get("minecraft:chicken"));
    EntityTypes.a<Entity> a = EntityTypes.a.a(Chestpet::new, EnumCreatureType.CREATURE);
    entityTypes = IRegistry.a(IRegistry.ENTITY_TYPE, "chestpet", a.a("chestpet"));
  }

  @Override
  public void onLoad() {
    registerEntity();
  }

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
    DataContainer.saveData(chunkKeys, beltManagers);
    if(cursedEarth!=null) cursedEarth.save();
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("matty")) {
      Player player = (Player) sender;
      CraftWorld world = (CraftWorld) player.getLocation().getWorld();
      World nmsWorld = world.getHandle();
      Location location = player.getLocation();
      Entity entity = entityTypes.createCreature(nmsWorld, null, null, null, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), null, false, false);
      nmsWorld.addEntity(entity, SpawnReason.CUSTOM);
      player.sendMessage("Done");
      return true;
    }
    if (command.getName().equals("setCursedSpreadRate")) {
      try {
        cursedEarth.setSpreadRate(Float.parseFloat(args[0]));
      }catch (Exception e){

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
