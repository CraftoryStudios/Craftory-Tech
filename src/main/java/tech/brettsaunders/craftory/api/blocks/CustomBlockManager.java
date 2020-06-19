package tech.brettsaunders.craftory.api.blocks;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockBreakEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockInteractEvent;
import tech.brettsaunders.craftory.api.blocks.events.CustomBlockPlaceEvent;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class CustomBlockManager implements Listener {

  private final static String DATA_FOLDER =
      Craftory.plugin.getDataFolder() + File.separator + "data";
  private final FileConfiguration customBlocksConfig;
  private final File customBlockConfigFile = new File(Craftory.plugin.getDataFolder(),
      "data/customBlockConfig.yml");
  private final HashMap<Location, CustomBlock> activeCustomBlocks;
  private final HashMap<Chunk, ArrayList<Location>> activeChunks;
  private final LRUCache<Chunk, ArrayList<CustomBlock>> customBlockLRUCache;
  private final HashMap<String, CustomBlockData> customBlockDataHashMap;

  public CustomBlockManager() {
    customBlocksConfig = YamlConfiguration.loadConfiguration(customBlockConfigFile);
    activeCustomBlocks = new HashMap<>();
    customBlockDataHashMap = new HashMap<>();
    activeChunks = new HashMap<>();
    customBlockLRUCache = new LRUCache<Chunk, ArrayList<CustomBlock>>(100) {
      @Override
      protected boolean removeEldestEntry(Entry eldest) {
        ArrayList<CustomBlock> customBlocks = (ArrayList<CustomBlock>) eldest.getValue();
        customBlocks.forEach((block -> saveCustomBlock(block)));
        return super.removeEldestEntry(eldest);
      }
    };
    loadCustomBlockData();
    Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    if (!CustomItemManager.isCustomBlockItem(e.getItemInHand())) {
      return;
    }
    final String customBlockItemName = CustomItemManager.getCustomItemName(e.getItemInHand());
    if (!customBlockDataHashMap.containsKey(customBlockItemName)) {
      return;
    }
    CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
        e.getBlockPlaced().getLocation(), customBlockItemName, e.getBlockPlaced());
    if (e.isCancelled()) {
      customBlockPlaceEvent.setCancelled(true);
    } else {
      CustomBlockData data = customBlockDataHashMap.get(customBlockItemName);
      Block block = e.getBlockPlaced();
      block.setType(Material.BROWN_MUSHROOM_BLOCK);

      BlockData blockData = block.getBlockData();
      MultipleFacing multipleFacing = (MultipleFacing) blockData;

      multipleFacing.setFace(
          BlockFace.UP, data.UP);
      multipleFacing.setFace(BlockFace.DOWN, data.DOWN);
      multipleFacing.setFace(BlockFace.NORTH, data.NORTH);
      multipleFacing.setFace(BlockFace.EAST, data.EAST);
      multipleFacing.setFace(BlockFace.SOUTH, data.SOUTH);
      multipleFacing.setFace(BlockFace.WEST, data.WEST);
      block.setBlockData(multipleFacing);

      activeCustomBlocks
          .put(block.getLocation(), new CustomBlock(block.getLocation(), customBlockItemName));
      addActiveChunk(block.getLocation());
    }
    Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onMushroomPhysics(BlockPhysicsEvent event) {
    if ((event.getChangedType() == Material.BROWN_MUSHROOM_BLOCK)) {
      event.setCancelled(true);
      event.getBlock().getState().update(true, false);
    }
  }

  @EventHandler
  public void onEnable(PluginEnableEvent e) {
    customBlocksConfig.addDefault("blocks", null);
    customBlocksConfig.options().copyDefaults(true);
    for (World world : Bukkit.getWorlds()) {
      for (Chunk chunk : world.getLoadedChunks()) {
        loadCustomBlock(world, chunk);
      }
    }
  }

  @EventHandler
  public void onDisable(PluginDisableEvent e) throws IOException {
    activeCustomBlocks.forEach(((location, block) -> {
      saveCustomBlock(block);
    }));
    customBlocksConfig.save(customBlockConfigFile);
  }


  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    final Location location = e.getBlock().getLocation();
    if (activeCustomBlocks.containsKey(location)) {
      //Return custom item
      final String blockName = activeCustomBlocks.get(
          location).blockName;
      CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(
          location, blockName);
      if (e.isCancelled()) {
        customBlockBreakEvent.setCancelled(true);
      } else {
        removeCustomBlock(activeCustomBlocks.get(location));
        removeIfLastActiveChunk(location);
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
          location.getWorld()
              .dropItemNaturally(location, CustomItemManager.getCustomItem(blockName, true));
        }
        activeCustomBlocks.remove(location);
      }
      Bukkit.getPluginManager().callEvent(customBlockBreakEvent);
      e.getBlock().setType(Material.AIR);
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent e) {
    if (customBlockLRUCache.containsKey(e.getChunk())) {
      ArrayList<CustomBlock> customBlocks = customBlockLRUCache.get(e.getChunk());
      customBlocks.forEach(block -> activeCustomBlocks.put(block.location, block));
      customBlockLRUCache.remove(e.getChunk());
    } else {
      loadCustomBlock(e.getWorld(), e.getChunk()); //TODO check so not called every chunk
    }
  }

  @EventHandler
  public void onChunkUnLoad(ChunkUnloadEvent e) {
    if (activeChunks.containsKey(e.getChunk())) {
      Bukkit.getLogger().info(e.getChunk().toString());
      ArrayList<CustomBlock> customBlocks = new ArrayList<>();
      for (Location location : activeChunks.get(e.getChunk())) {
        if (activeCustomBlocks.containsKey(location)) {
          customBlocks.add(activeCustomBlocks.get(location));
          activeCustomBlocks.remove(location);
        }
      }
      customBlockLRUCache.set(e.getChunk(), customBlocks);
      activeChunks.remove(e.getChunk());
    }
  }

  @EventHandler
  public void onPistonExtend(BlockPistonExtendEvent e) {
    e.getBlocks().forEach((block -> {
      if (activeCustomBlocks.containsKey(block.getLocation())) {
        e.setCancelled(true);
        return;
      }
    }));
  }

  @EventHandler
  public void onPistonRetract(BlockPistonRetractEvent e) {
    e.getBlocks().forEach(block -> {
      if (activeCustomBlocks.containsKey(block.getLocation())) {
        e.setCancelled(true);
        return;
      }
    });
  }

  @EventHandler
  public void onCustomBlockInteract(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
      if (activeCustomBlocks.containsKey(e.getClickedBlock().getLocation())) {
        CustomBlockInteractEvent customBlockInteractEvent = new CustomBlockInteractEvent(
            e.getAction(), e.getClickedBlock(), e.getBlockFace(), e.getItem(), e.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(customBlockInteractEvent);
      }
    }
    return;
  }

  /* API */
  //Only works in active chunk
  public boolean isCustomBlock(Location location) {
    return activeCustomBlocks.containsKey(location);
  }

  public String getCustomBlockName(Location location) {
    if (isCustomBlock(location)) {
      return activeCustomBlocks.get(location).blockName;
    }
    return "UNKNOWN";
  }

  public boolean isCustomBlockOfType(Location location, String typeName) {
    if (isCustomBlock(location)) {
      return getCustomBlockName(location).equals(typeName);
    }
    return false;
  }

  /* Internal Methods */
  private void saveCustomBlock(CustomBlock block) {
    //Get Region File
    int regionX = block.location.getChunk().getX() >> 5;
    int regionZ = block.location.getChunk().getZ() >> 5;
    String fileName = "r." + regionX + "," + regionZ + ".nbt";
    try {
      NBTFile nbtFile = new NBTFile(
          new File(DATA_FOLDER + File.separator + block.location.getWorld().getName(), fileName));

      //Get Chunk Section
      String chunkName = block.location.getChunk().getX() + "," + block.location.getChunk().getZ();
      NBTCompound chunk = nbtFile.addCompound(chunkName);

      //Get Location
      String locationName =
          block.location.getBlockX() + "," + block.location.getY() + "," + block.location.getZ();
      NBTCompound blockFileData = chunk.addCompound(locationName);

      //Save Data
      block.writeDataFile(blockFileData);
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void removeCustomBlock(CustomBlock block) {
    int regionX = block.location.getChunk().getX() >> 5;
    int regionZ = block.location.getChunk().getZ() >> 5;
    String fileName = "r." + regionX + "," + regionZ + ".nbt";
    try {
      NBTFile nbtFile = new NBTFile(
          new File(DATA_FOLDER + File.separator + block.location.getWorld().getName(), fileName));

      String chunkName = block.location.getChunk().getX() + "," + block.location.getChunk().getZ();
      NBTCompound chunk = nbtFile.getCompound(chunkName);
      if (chunk != null) {
        String locationName =
            block.location.getBlockX() + "," + block.location.getY() + "," + block.location.getZ();
        if (chunk.hasKey(locationName)) {
          chunk.removeKey(locationName);
        }
      }
      nbtFile.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadCustomBlock(World world, Chunk chunk) {
    int regionX = chunk.getX() >> 5;
    int regionZ = chunk.getZ() >> 5;
    String fileName = "r." + regionX + "," + regionZ + ".nbt";
    try {
      NBTFile nbtFile = new NBTFile(
          new File(DATA_FOLDER + File.separator + world.getName(), fileName));
      if (nbtFile == null) {
        return;
      }
      String chunkName = chunk.getX() + "," + chunk.getZ();
      NBTCompound chunkCompound = nbtFile.getCompound(chunkName);
      if (chunkCompound == null) {
        return;
      }

      for (String keys : chunkCompound.getKeys()) {
        NBTCompound blockData = chunkCompound.getCompound(keys);
        String[] locationData = keys.split(",");
        Location location = new Location(world, Double.parseDouble(locationData[0]),
            Double.parseDouble(locationData[1]), Double.parseDouble(locationData[2]));
        activeCustomBlocks
            .put(location, new CustomBlock(location, blockData.getString("BLOCK_NAME")));
        addActiveChunk(location);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  private void addActiveChunk(Location location) {
    ArrayList<Location> locations = activeChunks
        .getOrDefault(location.getChunk(), new ArrayList<>());
    locations.add(location);
    activeChunks.put(location.getChunk(), locations);
  }

  private void removeIfLastActiveChunk(Location location) {
    if (activeChunks.containsKey(location.getChunk())) {
      ArrayList<Location> locations = activeChunks.get(location.getChunk());
      if (locations.contains(location)) {
        if (locations.size() == 1) {
          activeChunks.remove(location.getChunk());
        } else {
          locations.remove(location);
          activeChunks.put(location.getChunk(), locations);
        }
      }
    }
  }

  private void loadCustomBlockData() {
    ConfigurationSection blocks = customBlocksConfig.getConfigurationSection("blocks");
    if (blocks == null) {
      return;
    }
    for (String key : blocks.getKeys(false)) {
      ConfigurationSection block = customBlocksConfig.getConfigurationSection("blocks." + key);
      CustomBlockData data = new CustomBlockData(block.getBoolean("UP"), block.getBoolean("DOWN"),
          block.getBoolean("NORTH"), block.getBoolean("EAST"), block.getBoolean("SOUTH"),
          block.getBoolean("WEST"));
      customBlockDataHashMap.put(key, data);
    }
  }
}