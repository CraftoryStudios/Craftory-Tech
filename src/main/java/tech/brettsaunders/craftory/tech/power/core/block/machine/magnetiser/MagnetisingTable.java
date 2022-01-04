/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.block.machine.magnetiser;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Constants.Blocks;
import tech.brettsaunders.craftory.Constants.Items;
import tech.brettsaunders.craftory.Constants.Sounds;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.events.Events;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.core.tools.ToolManager;
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class MagnetisingTable extends CustomBlock implements Listener {

    private static final HashMap<String, String> recipes = RecipeUtils.getMagnetiserRecipes();
    private static final int PROCESS_TIME = 10;
    @Persistent
    protected Boolean framePlaced;
    @Persistent
    protected ItemStack frameItem; //Only used for saving
    protected ItemFrame itemFrame;
    protected Location frameLocation;
    @Persistent
    protected int progress;
    private Chunk blockChunk;
    private boolean hasChunkLoaded;

    /* Construction */
    public MagnetisingTable(Location location, Player p) {
        super(location, Blocks.MAGNETISING_TABLE);
        Events.registerEvents(this);
        progress = 0;
        framePlaced = false;
    }

    /* Saving, Setup and Loading */
    public MagnetisingTable() {
        super();
        Events.registerEvents(this);
    }

    @Override
    public void blockBreak() {
        super.blockBreak();
        if (itemFrame != null) {
            if (itemFrame.getItem().getType() != Material.AIR) {
                itemFrame.getLocation().getWorld()
                        .dropItemNaturally(itemFrame.getLocation(), itemFrame.getItem());
            }
            itemFrame.remove();
            framePlaced = false;
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    public void afterLoadUpdate() {
        super.afterLoadUpdate();
        blockChunk = location.getChunk();
    }

    @Override
    public void beforeSaveUpdate() {
        super.beforeSaveUpdate();
        if (framePlaced) {
            if (itemFrame == null && (!findFrame() || !framePlaced)) return;
            frameItem = itemFrame.getItem();
            itemFrame.remove();
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent chunk) {
        Chunk loadedChunk = chunk.getChunk();
        if (loadedChunk.getX() == blockChunk.getX() && loadedChunk.getZ() == blockChunk.getZ())
            if (framePlaced && !hasChunkLoaded) {
                frameLocation = location.clone().add(0.5, 1.03125, 0.5);
                frameLocation.setPitch(-90);
                if (spawnFrame()) {
                    itemFrame.setItem(frameItem);
                    hasChunkLoaded = true;
                } else {
                    framePlaced = false;
                }
            }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent chunk) {
        Chunk loadedChunk = chunk.getChunk();
        if (loadedChunk.getX() == blockChunk.getX() && loadedChunk.getZ() == blockChunk.getZ())
            if (framePlaced && hasChunkLoaded) {
                if (itemFrame == null && (!findFrame() || !framePlaced)) return;
                frameItem = itemFrame.getItem();
                itemFrame.remove();
                hasChunkLoaded = false;
            }
    }

    private boolean spawnFrame() {
        frameLocation = location.clone().add(0.5, 1.03125, 0.5);
        frameLocation.setPitch(-90);
        if (!frameLocation.getBlock().getType().equals(Material.AIR)) {
            framePlaced = false;
            return false;
        }
        try {
            itemFrame = location.getWorld().spawn(frameLocation, ItemFrame.class);
            itemFrame.setFacingDirection(BlockFace.UP);
            itemFrame.setVisible(false);
            framePlaced = true;
            hasChunkLoaded = true;
            return true;
        } catch (IllegalArgumentException e) {
            Log.warn("ItemFrame error caught.");
            Log.debug(e.toString());
            framePlaced = false;
            return false;
        }
    }

    private boolean findFrame() {
        for (Entity entity : frameLocation.getWorld().getNearbyEntities(frameLocation, 2, 2, 2)) {
            if (entity instanceof ItemFrame && entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation().equals(location)) {
                itemFrame = (ItemFrame) entity;
                itemFrame.setFacingDirection(BlockFace.UP);
                itemFrame.setVisible(false);
                framePlaced = true;
                return true;
            }
        }
        return false;
    }

    private boolean frameHit() {
        if (itemFrame.getItem().getType().equals(Material.AIR)) {
            return true;
        }
        String itemName = CustomItemManager.getCustomItemName(itemFrame.getItem());
        if (recipes.containsKey(itemName)) {
            progress += 1;
            frameLocation.getWorld().spawnParticle(Particle.SPELL_INSTANT, frameLocation, 5);
            if (progress == PROCESS_TIME) {
                itemFrame.setItem(CustomItemManager.getCustomItem(recipes.get(itemName)));
                frameLocation.getWorld().spawnParticle(Particle.SMOKE_LARGE, frameLocation, 10);
                progress = 0;
                frameLocation.getWorld().playSound(frameLocation, Sounds.HAMMER_DOUBLE_HIT, SoundCategory.BLOCKS, 1f, 1f);
            } else {
                frameLocation.getWorld().playSound(frameLocation, Sounds.HAMMER_HIT, SoundCategory.BLOCKS, 1f, 1f);
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void itemFrameHit(EntityDamageByEntityEvent event) {
        if (frameLocation == null || itemFrame == null) {
            return;
        }
        if (!(event.getDamager().getType().equals(EntityType.PLAYER))) {
            return;
        }
        ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            return;
        }
        if (!CustomItemManager.getCustomItemName(itemStack).equals(Items.ENGINEERS_HAMMER)) {
            return;
        }
        if (!event.getEntityType().equals(EntityType.ITEM_FRAME)) {
            return;
        }
        if (!event.getEntity().getLocation().equals(frameLocation)) {
            return;
        }
        boolean hit = frameHit();
        if (hit) {
            event.setCancelled(true);
            ((Player) event.getDamager()).getInventory().setItemInMainHand(ToolManager.decreaseDurability(itemStack, 1));
        }
    }

    @EventHandler
    public void frameBreak(HangingBreakEvent event) {
        if (!framePlaced) {
            return;
        }
        if (event.getEntity().getLocation().equals(frameLocation)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockRightClicked(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getPlayer().isSneaking()) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (!event.getClickedBlock().getLocation().equals(location)) {
            return;
        }
        event.setCancelled(true);
        if (!framePlaced) {
            spawnFrame();
        }
        if (itemFrame != null) {
            if (!(itemFrame.getItem().getType().equals(Material.AIR))) {
                return;
            }
            ItemStack item = event.getItem().clone();
            event.getItem().setAmount(item.getAmount() - 1);
            item.setAmount(1);
            frameItem = item;
            itemFrame.setItem(item);
        }
    }
}
