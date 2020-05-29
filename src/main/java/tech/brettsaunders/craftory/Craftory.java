package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.SentryOptions;
import io.sentry.event.User;
import io.sentry.event.UserBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.brettsaunders.craftory.magic.mobs.chestpet.MagicMobManager;
import tech.brettsaunders.craftory.magic.mobs.chestpet.ChestPetTrait;
import tech.brettsaunders.craftory.multiBlock.MultiBlockManager;
import tech.brettsaunders.craftory.tech.belts.BeltEvents;
import tech.brettsaunders.craftory.tech.belts.BeltManager;
import tech.brettsaunders.craftory.tech.belts.DebugEvents;
import tech.brettsaunders.craftory.tech.belts.EntitySerach;
import tech.brettsaunders.craftory.tech.power.core.manager.PowerConnectorManager;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;
import tech.brettsaunders.craftory.tech.power.core.manager.TickableBaseManager;
import tech.brettsaunders.craftory.utils.Logger;


public final class Craftory extends JavaPlugin {

  public static SentryClient sentry;

  public static HashSet<Long> chunkKeys = new HashSet<>();
  public static HashMap<Location, BeltManager> beltManagers = new HashMap<>();
  FileConfiguration config = getConfig();

  private static Craftory plugin = null;
  public static TickableBaseManager tickableBaseManager = null;
  public static PowerConnectorManager powerConnectorManager = null;

  private CursedEarth cursedEarth = null;
  private Barrel barrel = null;
  private MagicMobManager magicMobManager = null;
  private Magic magic = null;
  private MultiBlockManager multiBlockManager;
  private static boolean debugMode = false;
  private static PoweredBlockManager blockPoweredManager = null;

  private static final String VERSION = "0.0.1";

  @Override
  public void onEnable() {
    // Plugin startup logic
    plugin = this;
    //Sentry
    Sentry.init("https://6b3f8706e5e74f39bbd037a30e3841f7@o399729.ingest.sentry.io/5257818?debug=false&&environment=WIP&&release="+VERSION);
    sentry = SentryClientFactory.sentryClient();
    //Setup
    resourceSetup();
    Sentry.getContext().setUser(new UserBuilder().setId(config.getString("serverUUID")).build());
    Sentry.getContext().addTag("BukkitVersion", Bukkit.getBukkitVersion());
    Sentry.getContext().addExtra("Plugins", Bukkit.getPluginManager().getPlugins());
    this.debugMode = config.getBoolean("debugMode");

    //Register
    String dataFolder = getDataFolder().getPath();

    //General Classes
    blockPoweredManager = new PoweredBlockManager();
    tickableBaseManager = new TickableBaseManager();
    powerConnectorManager = new PowerConnectorManager(); //TODO Loading
    multiBlockManager = new MultiBlockManager(dataFolder);

    //Magic Classes
    if (config.getBoolean("enableMagic")) {
      //Create Classes
      cursedEarth = new CursedEarth(dataFolder);
      magicMobManager = new MagicMobManager(dataFolder);
      barrel = new Barrel(dataFolder);
      magic = new Magic(magicMobManager);

      //Register Events
      getServer().getPluginManager().registerEvents(cursedEarth, this);
      getServer().getPluginManager().registerEvents(magic, this);
      getServer().getPluginManager().registerEvents(barrel, this);
      getServer().getPluginManager().registerEvents(powerConnectorManager, this);

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
  }

  @Override
  public void onDisable() {
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
        return true;
      } catch (Exception e) {
        if (sender instanceof Player) {
          ((Player) sender).sendMessage("Invalid use of cursed earth spread setting command");
        } else {
          Logger.warn("Invalid use of cursed earth spread setting command");
        }
      }
    }

    if (command.getName().equals("toggleDebugMode")) {
      debugMode = !debugMode;
      sender.sendMessage("Mode switch to " + debugMode);
      Sentry.capture("AHHHHH");
      return true;
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
    config.addDefault("serverUUID", UUID.randomUUID().toString());
    config.options().copyDefaults(true);
    saveConfig();

    File items = new File(getDataFolder().getParentFile(), "ItemsAdder/data/");
    items.mkdirs();
    FileUtils.copyResourcesRecursively(getClass().getResource("/data"), items);
  }

  public static Craftory getInstance() {
    return plugin;
  }

  public static boolean getDebugMode() { return debugMode; }

  public static PoweredBlockManager getBlockPoweredManager() { return blockPoweredManager; }

}
