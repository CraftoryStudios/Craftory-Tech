package tech.brettsaunders.craftory.tech.power.core.tools;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ClientCommand;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.brettsaunders.craftory.CoreHolder.PoweredToolType;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.PacketWrapper.WrapperPlayServerEntityDestroy;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.api.tasks.Tasks;
import tech.brettsaunders.craftory.tech.power.api.effect.NewBeam;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredToolManager implements Listener {

  private ObjectOpenHashSet<String> poweredTools = new ObjectOpenHashSet<>();
  public static String CHARGE_KEY = "Charge";
  public static String MAX_CHARGE_KEY = "MaxCharge";
  private Object2ObjectOpenHashMap<UUID, BlockFace> lastHitFace = new Object2ObjectOpenHashMap<>();
  private static int TOOL_POWER_COST = 100;
  private static ArrayList<Material> excavatorBlocks = new ArrayList<Material>(){
    {
      add(Material.SAND);
      add(Material.GRAVEL);
      add(Material.DIRT);
      add(Material.GRASS_BLOCK);
    }
  };

  private static ArrayList<PoweredToolType> fastTools = new ArrayList<PoweredToolType>(){
    {
      add(PoweredToolType.DRILL);
    }
  };

  private static ArrayList<PoweredToolType> slowTools = new ArrayList<PoweredToolType>(){
    {
      add(PoweredToolType.EXCAVATOR);
      add(PoweredToolType.HAMMER);
    }
  };

  public PoweredToolManager() {
    Events.registerEvents(this);
    addPacketListeners();
    poweredTools.add("diamond_drill");
    poweredTools.add("diamond_power_hammer");
    poweredTools.add("diamond_chainsaw");
    poweredTools.add("diamond_excavator");
  }



  //TODO remove later this is for testing
  @EventHandler
  public void rightCLick(PlayerInteractEvent e) {
    if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    ItemStack i = e.getItem();
    if(i==null || i.getType()!=Material.CHARCOAL) return;
    Logger.info("Calling the method");
    WrapperPlayServerEntityDestroy destroy =  NewBeam.spawnBeam(e.getPlayer(),e.getClickedBlock().getLocation().clone().add(0.5,0.5,0.5),e.getClickedBlock().getLocation().clone().add(3.5,0.5,3.5));
    Tasks.runTaskLater(() -> destroy.sendPacket(e.getPlayer()), 300);
  }



  private void addPacketListeners() {
    Craftory.packetManager.addPacketListener(new PacketAdapter(Craftory.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CLOSE_WINDOW) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        if(packet.getIntegers().read(0)==0){ //It is an inventory
          Player player = event.getPlayer();
          ItemStack itemStack = player.getInventory().getItemInMainHand();
          Tasks.runTaskLater(() -> addPotionEffects(itemStack, player), 1);
        }

      }
    });

    //Open inv packet not working
    Craftory.packetManager.addPacketListener(new PacketAdapter(Craftory.plugin,
        ListenerPriority.NORMAL,
        PacketType.Play.Client.CLIENT_COMMAND) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_COMMAND) {
          PacketContainer packet = event.getPacket();
          ClientCommand action = packet.getClientCommands().read(0);
          Logger.info(action.toString());
          if (action == ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            Player player = event.getPlayer();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Tasks.runTaskLater(() -> {
              removePotionEffects(itemStack, player);
              Logger.info("removed potion effects");
            }, 1);
          }
        }
      }
    });

  }


  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    addPotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }

  /*@EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    hidePotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }*/ //Needs to trigger when player opens their inventory

  @EventHandler
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
    removePotionEffects(itemStack, event.getPlayer());
    itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
    addPotionEffects(itemStack, event.getPlayer());
  }

  //Not needed with set on close packet
  /*@EventHandler
  public void onDrag(InventoryDragEvent event) {
    Player player = (Player) event.getWhoClicked();
    ItemStack itemStack = event.getNewItems().values().iterator().next();
    if(itemStack==null) return;
    for(int slot: event.getInventorySlots()){
      if(player.getInventory().getHeldItemSlot()==slot) {
        addPotionEffects(itemStack, player);
      }
    }
  } */

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    PlayerInventory inventory = player.getInventory();
    if(event.isShiftClick()) {
      if(inventory.getItemInMainHand().getType().equals(Material.AIR)){
        //Dont add with close packet working
        /*
        if(event.getSlotType().equals(SlotType.QUICKBAR)) return;
        boolean onlyHeldFree = true;
        for (int i = 0; i < player.getInventory().getHeldItemSlot(); i++) {
          if(inventory.getItem(i)==null || inventory.getItem(i).getType()==Material.AIR){
            onlyHeldFree = false;
            break;
          }
        }
        if(onlyHeldFree) {
          ItemStack itemStack = player.getInventory().getItem(event.getSlot());
          addPotionEffects(itemStack, player);
        } */
      } else {
        if(player.getInventory().getHeldItemSlot()==event.getSlot()){
          ItemStack itemStack = inventory.getItemInMainHand();
          removePotionEffects(itemStack,player);
        }
      }
      return;
    }
    if(event.getHotbarButton()!=-1){
      if(player.getInventory().getHeldItemSlot()==event.getHotbarButton()) { //Moving item in
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        removePotionEffects(itemStack,player);
        //Dont add with close packet working
        //itemStack = player.getInventory().getItem(event.getSlot());
        //addPotionEffects(itemStack, player);

      }
    } else {
      if(player.getInventory().getHeldItemSlot()==event.getSlot()) { //Moving item in
        ItemStack itemStack = player.getInventory().getItem(event.getSlot());
        removePotionEffects(itemStack,player);
        //Dont add with close packet working
        //itemStack = event.getCursor();
        //addPotionEffects(itemStack, player);
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
    lastHitFace.put(event.getPlayer().getUniqueId(),event.getBlockFace());
  }

  @EventHandler
  public void ToolBlockBreak(BlockBreakEvent event) {
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
    charge -=TOOL_POWER_COST;
    PoweredToolType toolType = getToolType(name);
    if(toolType==PoweredToolType.HAMMER) {
      ArrayList<Block> blocks = get2DNeighbours(event.getBlock(),lastHitFace.get(event.getPlayer().getUniqueId()));
      //ArrayList<ItemStack> drops = new ArrayList<>();
      for(Block block: blocks) {
        Collection<ItemStack> blockDrops = block.getDrops(tool);
        if(!blockDrops.isEmpty() && !excavatorBlocks.contains(block.getType()) && charge >= TOOL_POWER_COST) {
          block.breakNaturally(tool);
          //drops.addAll(drops);
          charge -=TOOL_POWER_COST;
        }
      }
      /*for (ItemStack stack: drops){
        event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(),stack);
      } */
    } else if(toolType==PoweredToolType.EXCAVATOR) {
      ArrayList<Block> blocks = get2DNeighbours(event.getBlock(),lastHitFace.get(event.getPlayer().getUniqueId()));
      //ArrayList<ItemStack> drops = new ArrayList<>();
      for(Block block: blocks) {
        Collection<ItemStack> blockDrops = block.getDrops(tool);
        if(!blockDrops.isEmpty() && charge >= TOOL_POWER_COST) {
          block.breakNaturally(tool);
          //drops.addAll(drops);
          charge -=TOOL_POWER_COST;
        }
      }
      /*for (ItemStack stack: drops){
        event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(),stack);
      } */
    }
    tool = setCharge(tool, charge);
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
    String type = itemStack.getType().toString();
    return type.endsWith("PICKAXE") || type.endsWith("SHOVEL") || type.endsWith("AXE") || type.endsWith("HOE");
  }

  public static boolean isPoweredTool(ItemStack itemStack) {
    if(!isTool(itemStack)) return false;
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
      if(block!=null && block.getType()!= Material.AIR) {
        blocks.add(block);
      }
    }
    return blocks;
  }


}
