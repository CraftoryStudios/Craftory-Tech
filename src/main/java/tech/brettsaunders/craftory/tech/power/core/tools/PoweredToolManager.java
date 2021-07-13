package tech.brettsaunders.craftory.tech.power.core.tools;

import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.brettsaunders.craftory.Constants.PoweredToolType;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class PoweredToolManager implements Listener {

  private Set<String> poweredTools = new HashSet<>();
  public static final String CHARGE_KEY = "Charge";
  public static final String MAX_CHARGE_KEY = "MaxCharge";
  private Map<UUID, BlockFace> lastHitFace = new HashMap<>();
  private static final int TOOL_POWER_COST = 100;
  private static final Set<Material> excavatorBlocks = new HashSet<>();
  private static final Set<PoweredToolType> fastTools = new HashSet<>();
  private static final Set<PoweredToolType> slowTools = new HashSet<>();

  private static final int VERSION = Integer.parseInt(Craftory.plugin.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3].substring(1).split("_")[1]);

  static {
    if (Craftory.isCaveAndCliffsUpdate) {
      excavatorBlocks.addAll(Tag.MINEABLE_SHOVEL.getValues());
    } else {
      if(VERSION > 16) {
        excavatorBlocks.add(Material.SOUL_SOIL);
      }
      excavatorBlocks.add(Material.CLAY);
      excavatorBlocks.add(Material.FARMLAND);
      excavatorBlocks.add(Material.GRASS_BLOCK);
      excavatorBlocks.add(Material.GRAVEL);
      excavatorBlocks.add(Material.MYCELIUM);
      excavatorBlocks.add(Material.PODZOL);
      excavatorBlocks.add(Material.COARSE_DIRT);
      excavatorBlocks.add(Material.DIRT);
      excavatorBlocks.add(Material.RED_SAND);
      excavatorBlocks.add(Material.SAND);
      excavatorBlocks.add(Material.SOUL_SAND);
      excavatorBlocks.add(Material.SNOW_BLOCK);
      excavatorBlocks.add(Material.SNOW);
    }

    fastTools.add(PoweredToolType.DRILL);

    slowTools.add(PoweredToolType.EXCAVATOR);
    slowTools.add(PoweredToolType.HAMMER);
  }

  public PoweredToolManager() {
    Events.registerEvents(this);
    //addPacketListeners();
  }

  public void addPoweredTool(String tool) {
    poweredTools.add(tool);
  }

//  private void addPacketListeners() {
//    Craftory.packetManager.addPacketListener(new PacketAdapter(Craftory.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CLOSE_WINDOW) {
//      @Override
//      public void onPacketReceiving(PacketEvent event) {
//        PacketContainer packet = event.getPacket();
//        if(packet.getIntegers().read(0)==0){ //It is an inventory
//          Player player = event.getPlayer();
//          ItemStack itemStack = player.getInventory().getItemInMainHand();
//          Tasks.runTaskLater(() -> addPotionEffects(itemStack, player), 1);
//        }
//
//      }
//    });
//  }


  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    addPotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }

  @EventHandler
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
    removePotionEffects(itemStack, event.getPlayer());
    itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
    addPotionEffects(itemStack, event.getPlayer());
  }

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    PlayerInventory inventory = player.getInventory();
    if(event.isShiftClick()) {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()){
        ItemStack itemStack = inventory.getItemInMainHand();
        removePotionEffects(itemStack,player);
      }
      return;
    }
    if(event.getHotbarButton()!=-1){
      if(player.getInventory().getHeldItemSlot()==event.getHotbarButton()) { //Moving item in
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        removePotionEffects(itemStack,player);
      }
    } else {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()) { //Moving item in
        ItemStack itemStack = player.getInventory().getItem(event.getSlot());
        removePotionEffects(itemStack,player);
      }
    }
  }

  @EventHandler
  public void onPlayerItemDrop(PlayerDropItemEvent event) {
    ItemStack itemStack = event.getItemDrop().getItemStack();
    if(!isPoweredTool(itemStack)) return;
    Player player = event.getPlayer();
    removePotionEffects(itemStack, player);
    addPotionEffects(player.getInventory().getItemInMainHand(), player);
  }

  @EventHandler
  public void onPlayerPickupItem(EntityPickupItemEvent event) {
    if(!(event.getEntity() instanceof Player)) return;
    ItemStack itemStack = event.getItem().getItemStack();
    if(!isPoweredTool(itemStack)) return;
    Player player = (Player) event.getEntity();
    PlayerInventory inventory = player.getInventory();
    if(!inventory.getItemInMainHand().getType().equals(Material.AIR)) return;
    boolean onlyHeldFree = true;
    for (int i = 0; i < player.getInventory().getHeldItemSlot(); i++) {
      if(inventory.getItem(i)==null || inventory.getItem(i).getType()==Material.AIR){
        onlyHeldFree = false;
        break;
      }
    }
    if(onlyHeldFree) {
      addPotionEffects(itemStack, player);
    }
  }

  @EventHandler
  public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    ItemStack itemStack = event.getOffHandItem();
    if (isPoweredTool(itemStack)) {
      removePotionEffects(itemStack, event.getPlayer());
    }
    itemStack = event.getMainHandItem();
    if (isPoweredTool(itemStack)) {
      addPotionEffects(itemStack, event.getPlayer());
    }
  }

  private void removePotionEffects(ItemStack itemStack, Player player) {
    if(itemStack==null) {
      return;
    }
    if(!isPoweredTool(itemStack)) return;
    String name = CustomItemManager.getCustomItemName(itemStack);
    PoweredToolType toolType = getToolType(name);
    if(slowTools.contains(toolType)) {
      player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    } else if(fastTools.contains(toolType)){
      player.removePotionEffect(PotionEffectType.FAST_DIGGING);
    }
  }

  private void addPotionEffects(ItemStack itemStack, Player player) {
    if(itemStack==null) {
      return;
    }
    if(!isPoweredTool(itemStack)) return;
    String name = CustomItemManager.getCustomItemName(itemStack);
    PoweredToolType toolType = getToolType(name);
    if(slowTools.contains(toolType)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,
          Integer.MAX_VALUE,0,
          false, false, false));
    } else if(fastTools.contains(toolType)){
      player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
          Integer.MAX_VALUE, 1,
          false, false, false));
    }
  }

  @EventHandler
  public void onPlayerBlockHit(PlayerInteractEvent event) {
    if(event.isCancelled()) return;
    if(event.getAction() == Action.LEFT_CLICK_BLOCK){
      lastHitFace.put(event.getPlayer().getUniqueId(),event.getBlockFace());
    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
      if(event.getClickedBlock().getType()!=Material.GRASS_BLOCK) return;
      ItemStack tool =  event.getPlayer().getInventory().getItemInMainHand();
      if(!isPoweredTool(tool)) return;
      String name = CustomItemManager.getCustomItemName(tool);
      PoweredToolType toolType = getToolType(name);
      if(toolType != (PoweredToolType.EXCAVATOR)) return;
      NBTItem nbt = new NBTItem(tool);
      int charge = nbt.getInteger(CHARGE_KEY);
      if(charge < TOOL_POWER_COST) {
        event.setCancelled(true);
        return;
      }
      charge -= TOOL_POWER_COST;
      List<Block> blocks = get2DNeighbours(event.getClickedBlock(),event.getBlockFace());
      for(Block block: blocks) {
        if(block.getType()==Material.GRASS_BLOCK && charge >= TOOL_POWER_COST) {
          block.setType(Material.DIRT_PATH);
          charge -=TOOL_POWER_COST;
        }
      }
      if(event.getPlayer().getGameMode() != GameMode.CREATIVE) tool = setCharge(tool, charge);
      event.getPlayer().getInventory().setItemInMainHand(tool);
    }
  }

  @EventHandler
  public void toolBlockBreak(BlockBreakEvent event) {
    if(event.isCancelled()) return;
    ItemStack tool =  event.getPlayer().getInventory().getItemInMainHand();
    if(!isPoweredTool(tool)) return;
    String name = CustomItemManager.getCustomItemName(tool);
    if(!poweredTools.contains(name)) return;
    NBTItem nbt = new NBTItem(tool);
    int charge = nbt.getInteger(CHARGE_KEY);
    if(charge < TOOL_POWER_COST) {
      event.setCancelled(true);
      return;
    }
    toolUse(tool, name, charge, event);
  }

  private void toolUse(ItemStack tool, String name, int charge, BlockBreakEvent event) {
    charge -=TOOL_POWER_COST;
    PoweredToolType toolType = getToolType(name);
    if(toolType==PoweredToolType.HAMMER) {
      List<Block> blocks = get2DNeighbours(event.getBlock(),lastHitFace.get(event.getPlayer().getUniqueId()));
      for(Block block: blocks) {
        Collection<ItemStack> blockDrops = block.getDrops(tool);
        if(!blockDrops.isEmpty() && !excavatorBlocks.contains(block.getType()) && charge >= TOOL_POWER_COST) {
          block.breakNaturally(tool);
          charge -=TOOL_POWER_COST;
        }
      }
    } else if(toolType==PoweredToolType.EXCAVATOR) {
      List<Block> blocks = get2DNeighbours(event.getBlock(),lastHitFace.get(event.getPlayer().getUniqueId()));
      for(Block block: blocks) {
        Collection<ItemStack> blockDrops = block.getDrops(tool);
        if(!blockDrops.isEmpty() && charge >= TOOL_POWER_COST) {
          block.breakNaturally(tool);
          charge -=TOOL_POWER_COST;
        }
      }
    }
    if(event.getPlayer().getGameMode() != GameMode.CREATIVE) tool = setCharge(tool, charge);
    event.getPlayer().getInventory().setItemInMainHand(tool);
  }

  public static ItemStack setCharge(ItemStack tool, int charge) {
    NBTItem nbt = new NBTItem(tool);
    nbt.setInteger(CHARGE_KEY, charge);
    tool = nbt.getItem();
    ArrayList<String> lore = new ArrayList<>();
    lore.add("Charge: " + Utilities.rawEnergyToPrefixed(charge) + "/" + Utilities.rawEnergyToPrefixed(nbt.getInteger(MAX_CHARGE_KEY)));
    ItemMeta meta = tool.getItemMeta();
    meta.setLore(lore);
    tool.setItemMeta(meta);
    return tool;
  }


  public static PoweredToolType getToolType(String name) {
    for(PoweredToolType type: PoweredToolType.values()){
      if(name.endsWith(type.getItemSuffix())) return type;
    }
    return null;
  }

  public static boolean isTool(ItemStack itemStack) {
    if (itemStack==null) return false;
    String type = itemStack.getType().toString();
    return type.endsWith("PICKAXE") || type.endsWith("SHOVEL") || type.endsWith("AXE") || type.endsWith("HOE");
  }

  public static boolean isPoweredTool(ItemStack itemStack) {
    if (!isTool(itemStack)) return false;
    NBTItem nbtItem = new NBTItem(itemStack);
    return nbtItem.hasKey(CHARGE_KEY) && nbtItem.hasKey(MAX_CHARGE_KEY);
  }

  private ArrayList<Block> get2DNeighbours(Block centerBlock, BlockFace face) {
    ArrayList<Location> locations = new ArrayList<>();
    ArrayList<Block> blocks = new ArrayList<>();
    Location center = centerBlock.getLocation();
    if(face==BlockFace.UP || face==BlockFace.DOWN) {
      locations.add(center.clone().add(1,0,0));
      locations.add(center.clone().add(0,0,1));
      locations.add(center.clone().add(-1,0,0));
      locations.add(center.clone().add(0,0,-1));
      locations.add(center.clone().add(1,0,1));
      locations.add(center.clone().add(1,0,-1));
      locations.add(center.clone().add(-1,0,1));
      locations.add(center.clone().add(-1,0,-1));
    } else if (face==BlockFace.NORTH || face==BlockFace.SOUTH) {
      locations.add(center.clone().add(1,0,0));
      locations.add(center.clone().add(0,1,0));
      locations.add(center.clone().add(-1,0,0));
      locations.add(center.clone().add(0,-1,0));
      locations.add(center.clone().add(1,1,0));
      locations.add(center.clone().add(1,-1,0));
      locations.add(center.clone().add(-1,1,0));
      locations.add(center.clone().add(-1,-1,0));
    } else if (face==BlockFace.EAST || face==BlockFace.WEST) {
      locations.add(center.clone().add(0,1,0));
      locations.add(center.clone().add(0,0,1));
      locations.add(center.clone().add(0,-1,0));
      locations.add(center.clone().add(0,0,-1));
      locations.add(center.clone().add(0,1,1));
      locations.add(center.clone().add(0,1,-1));
      locations.add(center.clone().add(0,-1,1));
      locations.add(center.clone().add(0,-1,-1));
    }
    for(Location loc: locations) {
      Block block = loc.getBlock();
      if(block.getType() != Material.AIR) {
        blocks.add(block);
      }
    }
    return blocks;
  }


}
