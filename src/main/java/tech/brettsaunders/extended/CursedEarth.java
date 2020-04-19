package tech.brettsaunders.extended;

import com.google.common.collect.ConcurrentHashMultiset;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Location;
import java.util.Random;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CursedEarth implements Listener, Runnable {

    private HashSet<Block> earths = new HashSet<>();
    private HashSet<Block> closedList = new HashSet<>();
    BlockUtils bs = new BlockUtils();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Extended.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Extended.plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        if (bs.isCustomBlockType(event.getBlockPlaced(), "extended:cursed_earth")) {
                            earths.add(event.getBlockPlaced());
                        }
                    }
                }, 1L);
    }

    BlockFace[] faces = {BlockFace.SELF, BlockFace.NORTH, BlockFace.NORTH_EAST,  BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    @Override
    public void run() {
        Random random = new Random();
        HashSet<Block> toAdd = new HashSet<>();
        HashSet<Block> toRemove = new HashSet<>();
        int empty = 0;
        for (Block block : earths) {
            if(random.nextInt(4)>= 1) {continue;}
            for (int i = -1; i < 2; i++) {
                Block search = block.getRelative(0, i, 0);
                ArrayList<BlockFace> valid = generateValidFaces(search);
                if (valid.size() > 0){
                    BlockFace face = valid.get(random.nextInt(valid.size()));
                    if(random.nextInt(5000) <= (30 * valid.size())){
                        ItemsAdder.placeCustomBlock(search.getRelative(face).getLocation(), ItemsAdder.getCustomItem("extended:cursed_earth"));
                        toAdd.add(search.getRelative(face));
                    }
                } else { empty +=1;}
            }
            if(empty==3){
                closedList.add(block);
                toRemove.add(block);
            }
            empty = 0;
        }
        earths.addAll(toAdd);
        earths.removeAll(toRemove);
    }

    private ArrayList<BlockFace> generateValidFaces(Block block){
        ArrayList<BlockFace> valid = new ArrayList<>();
        for (BlockFace face : faces) {
            Block neighbour = block.getRelative(face);
            if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder.isCustomBlock(neighbour)) {
                valid.add(face);
            }
        }
        return valid;
    }
}
