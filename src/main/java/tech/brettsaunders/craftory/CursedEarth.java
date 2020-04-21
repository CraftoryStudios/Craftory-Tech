package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.Random;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashSet;

public class CursedEarth implements Listener, Runnable {

    private HashSet<Block> earths = new HashSet<>();
    private HashSet<Block> closedList = new HashSet<>();
    BlockUtils bs = new BlockUtils();
    static private float SPREAD_RATE = 1.0f;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Craftory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Craftory.plugin,
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
        for (Block block : earths) {
            if(random.nextInt(4)>= 1) {continue;} //Stops every block from spreading at the same time, could change this to select random elements rather than iterating and skipping
            ArrayList<Block> valid = generateValidFaces(block); //Get blocks that it can spread to
            if (valid.size() > 0) {
                if(random.nextInt(10000) / SPREAD_RATE <= (30 * valid.size())) {
                    Block neighbour = valid.get(random.nextInt(valid.size())); //Picks a random face
                    ItemsAdder.placeCustomBlock(neighbour.getLocation(), ItemsAdder.getCustomItem("extended:cursed_earth"));
                    toAdd.add(neighbour);
                    System.out.println("Cursed Spread");
                    break;
                }
            } else {
                System.out.println("No valid");
                closedList.add(block);
                toRemove.add(block);
            }
        }
        earths.addAll(toAdd);
        earths.removeAll(toRemove);
    }

    private ArrayList<Block> generateValidFaces(Block block){
        ArrayList<Block> valid = new ArrayList<>();
        Block blockup = block.getRelative(0, 1, 0);
        Block blockdown = block.getRelative(0, -1, 0);
        Block neighbour;
        for (BlockFace face : faces) {
            neighbour = block.getRelative(face);
            if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder.isCustomBlock(neighbour)) {
                valid.add(neighbour);
            }
            neighbour = blockup.getRelative(face);
            if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder.isCustomBlock(neighbour)) {
                valid.add(neighbour);
            }
            neighbour = blockdown.getRelative(face);
            if (!neighbour.isEmpty() && neighbour.getType().isBlock() && !ItemsAdder.isCustomBlock(neighbour)) {
                valid.add(neighbour);
            }
        }
        return valid;
    }
}
