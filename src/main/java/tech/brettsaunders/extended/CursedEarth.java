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
    static private float SPREAD_RATE = 1.0f;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Extended.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Extended.plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        if (bs.isCustomBlockType(event.getBlockPlaced(), "extended:cursed_earth")) {
                            earths.add(event.getBlockPlaced()); //Add the block to the HashSet when it is placed
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
            if(random.nextInt(4)>= 1) {continue;} //Stops every block from spreading at the same time, could change this to select random elements rather than iterating and skipping
            for (int i = -1; i < 2; i++) { //Loop through y-1, y and y+1 of block
                Block search = block.getRelative(0, i, 0);
                ArrayList<BlockFace> valid = generateValidFaces(search); //Get blocks that it can spread to
                if (valid.size() > 0 && random.nextInt(10000) / SPREAD_RATE <= (30 * valid.size())){
                    BlockFace face = valid.get(random.nextInt(valid.size())); //Picks a random face
                    ItemsAdder.placeCustomBlock(search.getRelative(face).getLocation(), ItemsAdder.getCustomItem("extended:cursed_earth"));
                    toAdd.add(search.getRelative(face));
                    break;
                } else { empty +=1;}
            }
            if(empty==3){ //If there are no valid spaces for it to spread to move it to the closed list
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
