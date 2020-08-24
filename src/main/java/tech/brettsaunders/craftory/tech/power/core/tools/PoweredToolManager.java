package tech.brettsaunders.craftory.tech.power.core.tools;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.Logger;

public class PoweredToolManager implements Listener {

  private ObjectOpenHashSet<String> poweredTools = new ObjectOpenHashSet<>();
  public static String CHARGE_KEY = "Charge";
  public static String MAX_CHARGE_KEY = "MaxCharge";
  private Object2ObjectOpenHashMap<UUID, BlockFace> lastHitFace = new Object2ObjectOpenHashMap<>();
  private static int TOOL_POWER_COST = 100;

  public PoweredToolManager() {
    Events.registerEvents(this);
    poweredTools.add("diamond_drill");
    poweredTools.add("diamond_power_hammer");
    poweredTools.add("diamond_chainsaw");
    poweredTools.add("diamond_excavator");
  }

  /*@EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    hidePotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }*/ //Needs to trigger when player opens their inventory

  /*EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    addPotionEffects(event.getPlayer().getInventory().getItemInMainHand(),
        (Player) event.getPlayer());
  }*/ // Needs to trigger when player closes their inventory

  @EventHandler
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
    hidePotionEffects(itemStack, event.getPlayer());
    itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
    addPotionEffects(itemStack,event.getPlayer());
  }

  private void hidePotionEffects(ItemStack itemStack, Player player) {
    if(itemStack==null || itemStack.getType()==Material.AIR) {
      return;
    }
    //TODO CHECK IF ITS A TOOL FIRST
    String name = CustomItemManager.getCustomItemName(itemStack);
    if(isHammer(name)) {
      player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    } else if(isDrill(name)){
      player.removePotionEffect(PotionEffectType.FAST_DIGGING);
    }
  }

  private void addPotionEffects(ItemStack itemStack, Player player) {
    if(itemStack==null || itemStack.getType()==Material.AIR) {
      return;
    }
    //TODO CHECK IF ITS A TOOL FIRST
    String name = CustomItemManager.getCustomItemName(itemStack);
    if(isHammer(name)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,
          Integer.MAX_VALUE,0,
          false, false, false));
    } else if(isDrill(name)){
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
    if(tool.getType()==Material.AIR) return;
    String name = CustomItemManager.getCustomItemName(tool);
    if(!poweredTools.contains(name)) return;
    NBTItem nbt = new NBTItem(tool);
    if(!nbt.hasKey(CHARGE_KEY)|| !nbt.hasKey(MAX_CHARGE_KEY)) return;
    int charge = nbt.getInteger(CHARGE_KEY);
    if(charge < TOOL_POWER_COST) {
      event.setCancelled(true);
      return;
    }
    charge -=TOOL_POWER_COST;
    if(isHammer(name)) {
      ArrayList<Block> blocks = getHammerBlocks(event.getBlock(),lastHitFace.get(event.getPlayer().getUniqueId()));
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
    lore.add("Charge: " + charge + "/" + nbt.getInteger(MAX_CHARGE_KEY));
    ItemMeta meta = tool.getItemMeta();
    meta.setLore(lore);
    tool.setItemMeta(meta);
    return tool;
  }

  private boolean isHammer(String name) {
    return name.endsWith("hammer");
  }

  private boolean isDrill(String name) { return name.endsWith("drill"); }

  private ArrayList<Block> getHammerBlocks(Block centerBlock, BlockFace face) {
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
