package tech.brettsaunders.craftory.tech.power.core.tools;

import static tech.brettsaunders.craftory.tech.power.core.tools.PoweredToolManager.TOOL_POWER_COST;

import io.github.bakedlibs.dough.protection.Interaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.Craftory;

public class Chainsaw {

  private static final Set<Material> chainsawBlocks = new HashSet<>();


  static {
    if (Craftory.isCaveAndCliffsUpdate) {
      chainsawBlocks.addAll(Tag.LOGS.getValues());
    }
  }



  protected static int handleChainsawBreak(BlockBreakEvent event, ItemStack tool, int charge, OfflinePlayer player) {
    Block root = event.getBlock();
    Material rootType = root.getType();
    if (!chainsawBlocks.contains(rootType)) return charge;
    Set<Block> blocks = findTreeBlocks(root, rootType);
    for (Block block: blocks) {
      if (Craftory.protectionManager.hasPermission(player, block, Interaction.BREAK_BLOCK)) {
        if (chainsawBlocks.contains(block.getType()) && charge >= TOOL_POWER_COST) {
          if (event.getPlayer().getGameMode()!= GameMode.CREATIVE) {
            block.breakNaturally(tool);
            charge -= TOOL_POWER_COST;
          } else {
            block.setType(Material.AIR);
          }
        }
      }
    }
    return charge;
  }

  private static Set<Block> findTreeBlocks(Block root, Material rootType) {
    Set<Block> blocks = new LinkedHashSet<>();
    List<Block> logsToCheck = new ArrayList<>();
    logsToCheck.add(root);
    Block nextLog;
    List<Block> neighbours;
    while (!logsToCheck.isEmpty()) {
      nextLog = logsToCheck.remove(0);
      neighbours = getValidNeighbours(nextLog, rootType);
      for (Block neighbour: neighbours) {
        if (!blocks.contains(neighbour)) {
          logsToCheck.add(neighbour);
        }
        blocks.add(neighbour);
      }
    }
    return blocks;
  }

  private static List<Block> getValidNeighbours(Block block, Material rootType) {
    List<Block> neighbours = new ArrayList<>();
    Block neighbour;
    for (int y = 0; y < 2; y++) {
      for (int x = -1; x < 2; x++) {
        for (int z = -1; z < 2; z++) {
          if (x == 0 && z == 0 && y == 0) continue;
          neighbour = block.getRelative(x, y, z);
          if (neighbour.getType() == rootType) {
            neighbours.add(neighbour);
          }
        }
      }
    }
    return neighbours;
  }

}
