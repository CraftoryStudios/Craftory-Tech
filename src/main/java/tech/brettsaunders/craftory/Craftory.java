package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.magic.mobs.chestpet.MagicMobManager;
import tech.brettsaunders.craftory.magic.mobs.chestpet.ChestPetTrait;
import tech.brettsaunders.craftory.multiBlock.MultiBlockManager;
import tech.brettsaunders.craftory.tech.belts.BeltEvents;
import tech.brettsaunders.craftory.tech.belts.BeltManager;
import tech.brettsaunders.craftory.tech.belts.DebugEvents;
import tech.brettsaunders.craftory.tech.belts.EntitySerach;
import tech.brettsaunders.craftory.tech.power.core.block.BlockCell;
import tech.brettsaunders.craftory.tech.power.core.manager.BlockPoweredManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerManager;
import tech.brettsaunders.craftory.tech.power.core.manager.SolidFuelManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.Logger;


public final class Craftory extends JavaPlugin {

  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();
  FileConfiguration config = getConfig();

  private static Craftory plugin = null;
  private static PowerManager powerManager = null;
  public static TickableBaseManager tickableBaseManager = null;

  private CursedEarth cursedEarth = null;
  private Barrel barrel = null;
  private MagicMobManager magicMobManager = null;
  private Magic magic = null;
  private MultiBlockManager multiBlockManager;
  private static boolean debugMode = false;
  private static BlockPoweredManager blockPoweredManager = null;

  @Override
  public void onEnable() {
    // Plugin startup logic
    plugin = this;
    resourceSetup();
    this.debugMode = config.getBoolean("debugMode");

    //Register
    String dataFolder = getDataFolder().getPath();

    //General Classes
    blockPoweredManager = new BlockPoweredManager();
    tickableBaseManager = new TickableBaseManager();
    multiBlockManager = new MultiBlockManager(dataFolder);

    //Add Fuel
    SolidFuelManager.addFuel(new ItemStack(Material.COAL), 100);

    //Magic Classes
    if (config.getBoolean("enableMagic")) {
      //Create Classes
      cursedEarth = new CursedEarth(dataFolder);
      magicMobManager = new MagicMobManager(dataFolder);
      barrel = new Barrel(dataFolder);
      magic = new Magic(magicMobManager);
      powerManager = new PowerManager();

      //Register Events
      getServer().getPluginManager().registerEvents(cursedEarth, this);
      getServer().getPluginManager().registerEvents(magic, this);
      getServer().getPluginManager().registerEvents(barrel, this);
      getServer().getPluginManager().registerEvents(powerManager, this);

      //Register Tasks
      getServer().getScheduler().scheduleSyncRepeatingTask(this, cursedEarth, 800L, 80L);

      //Register Mobs
      CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ChestPetTrait.class).withName("chestpet"));
    }

    //Tech Classes
    if (config.getBoolean("enableTech")) {
      getServer().getPluginManager().registerEvents(new BeltEvents(), this);
      getServer().getPluginManager().registerEvents(new DebugEvents(), this);
      getServer().getScheduler().scheduleSyncRepeatingTask(this, new EntitySerach(), 1L, 1L);

    }

    //OnEnables
    powerManager.onEnable();
    blockPoweredManager.onEnable();
    BlockCell.initialize();

  }

  @Override
  public void onDisable() {
    //OnDisalbe
    powerManager.onDisable();
    blockPoweredManager.onDisable();

    //Save Data
    DataContainer.saveData(chunkKeys, beltManagers);
    if (config.getBoolean("enableMagic")) {
      cursedEarth.save();
      barrel.save();
      magicMobManager.save();
    }
    multiBlockManager.save();
    // Plugin shutdown logic
    plugin = null;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("matty")) {
      if(!(sender instanceof Player))
        return true;

      Player player = (Player) sender;
      FontImageWrapper fontImageWrapper = new FontImageWrapper("mcguis:blank_menu");
      TexturedInventoryWrapper inventory = new TexturedInventoryWrapper(null,
          54,
          ChatColor.BLACK + "   Cell",fontImageWrapper);
      inventory.showInventory(player);
      inventory.getInternal().setItem(17, ItemsAdder.getCustomItem("extra:output"));

      return true;
    }
    if (command.getName().equals("setCursedSpreadRate")) {
      try {
        cursedEarth.setSpreadRate(Float.parseFloat(args[0]));
      } catch (Exception e) {
        if (sender instanceof Player) {
          ((Player) sender).sendMessage("Invalid use of cursed earth spread setting command");
        } else {
          Logger.warn("Invalid use of cursed earth spread setting command");
        }
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
    config.addDefault("debugMode", false);
    config.options().copyDefaults(true);
    saveConfig();

    File items = new File(getDataFolder().getParentFile(), "ItemsAdder/data/");
    items.mkdirs();
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"), items);
  }

  public static Craftory getInstance() {
    return plugin;
  }

  public static PowerManager getPowerManager() { return powerManager; }

  public static boolean getDebugMode() { return debugMode; }

  public static BlockPoweredManager getBlockPoweredManager() { return blockPoweredManager; }

}
