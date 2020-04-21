package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Magic implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!(e.hasItem())) return;
        if (!(ItemsAdder.matchCustomItemName(e.getItem(), "extended:wand"))) return;
        Block clicked = e.getClickedBlock();
        if (clicked.getType().equals(Material.CAULDRON)){
            wandUsedCauldron(clicked);
            return;
        }
        wandUsed(clicked);
    }

    private ItemStack[] getItemsInRadius(Location loc, float radius){
        return Arrays.stream(loc.getChunk().getEntities()).filter(e -> e instanceof Item).filter(e -> e.getLocation().distance(loc) <= radius).map(e -> ((Item) e).getItemStack()).toArray(ItemStack[]::new);
    }

    private void wandUsed(Block block) {
        float spell_range = 1.5f;
        System.out.println("Wand stuff");
        ItemStack[] items = getItemsInRadius(block.getLocation().add(0,1,0), spell_range);
        int amount;
        for(ItemStack i: items){
            amount = i.getAmount();
            amount *=2;
            amount = (amount>i.getMaxStackSize()) ? i.getMaxStackSize():amount;
            i.setAmount(amount);
        }
    }

    private void wandUsedCauldron(Block cauldron){
        System.out.println("Magic");
        Location loc = cauldron.getLocation();
        ItemStack[] items = getItemsInRadius(loc, 1.0f);
        int goldCount = 0;
        int redstoneCount = 0;
        for(ItemStack i: items){
            if (i.getType().equals(Material.GOLD_NUGGET)){
                goldCount += i.getAmount();
            } else if (i.getType().equals(Material.REDSTONE)){
                redstoneCount += i.getAmount();
            }
        }
        int glowstoneAmount = (goldCount < redstoneCount) ? goldCount : redstoneCount;
        System.out.println("Glow: " + glowstoneAmount);
        System.out.println("Red: " + redstoneCount);
        System.out.println("Gold: " + goldCount);
        if (glowstoneAmount < 1) return;

        int to_removeG = glowstoneAmount;
        int to_removeR = glowstoneAmount;
        for(ItemStack i: items){
            if (to_removeG>0 && i.getType().equals(Material.GOLD_NUGGET)){
                if(i.getAmount() > to_removeG){
                    i.setAmount(i.getAmount() - to_removeG);
                    to_removeG = 0;
                } else {
                    to_removeG -= i.getAmount();
                    i.setAmount(0);
                }
            }else if (to_removeR>0 && i.getType().equals(Material.REDSTONE)){
                if(i.getAmount() > to_removeR){
                    i.setAmount(i.getAmount() - to_removeR);
                    to_removeR = 0;
                } else {
                    to_removeR -= i.getAmount();
                    i.setAmount(0);
                }
            }
        }
        ItemStack item;
        while (glowstoneAmount > 0) {
            if (glowstoneAmount > 64) {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                item.setAmount(64);
                glowstoneAmount -= 64;
            } else {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                item.setAmount(glowstoneAmount);
                glowstoneAmount = 0;
            }
            cauldron.getWorld().dropItemNaturally(loc,item);
        }
    }
}
