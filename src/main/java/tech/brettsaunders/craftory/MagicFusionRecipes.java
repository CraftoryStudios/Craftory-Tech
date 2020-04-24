package tech.brettsaunders.craftory;

import java.util.HashMap;
import org.bukkit.Material;

public class MagicFusionRecipes {

  public static HashMap<String, Integer>[] getRecipe(HashMap<String, Integer> counts) {
    HashMap<String, Integer> inputs = new HashMap<>();
    HashMap<String, Integer> products = new HashMap<>();
    if (counts.containsKey(Material.GOLD_NUGGET.toString()) && counts
        .containsKey(Material.REDSTONE.toString())) {
      inputs.put(Material.GOLD_NUGGET.toString(), 1);
      inputs.put(Material.REDSTONE.toString(), 1);
      products.put(Material.GLOWSTONE_DUST.toString(), 1);
    } else if (counts.containsKey(Material.OAK_DOOR.toString()) && counts
        .containsKey(Material.SPRUCE_PLANKS.toString())) {
      inputs.put(Material.OAK_DOOR.toString(), 1);
      inputs.put(Material.SPRUCE_PLANKS.toString(), 3);
      products.put(Material.SPRUCE_DOOR.toString(), 1);
    } else if (counts.containsKey(Material.OAK_DOOR.toString()) && counts
        .containsKey(Material.JUNGLE_PLANKS.toString())) {
      inputs.put(Material.OAK_DOOR.toString(), 1);
      inputs.put(Material.JUNGLE_PLANKS.toString(), 3);
      products.put(Material.JUNGLE_DOOR.toString(), 1);
    } else if (counts.containsKey(Material.OAK_DOOR.toString()) && counts
        .containsKey(Material.BIRCH_PLANKS.toString())) {
      inputs.put(Material.OAK_DOOR.toString(), 1);
      inputs.put(Material.BIRCH_PLANKS.toString(), 3);
      products.put(Material.BIRCH_DOOR.toString(), 1);
    } else if (counts.containsKey(Material.OAK_DOOR.toString()) && counts
        .containsKey(Material.DARK_OAK_PLANKS.toString())) {
      inputs.put(Material.OAK_DOOR.toString(), 1);
      inputs.put(Material.DARK_OAK_PLANKS.toString(), 3);
      products.put(Material.DARK_OAK_DOOR.toString(), 1);
    } else if (counts.containsKey(Material.OAK_DOOR.toString()) && counts
        .containsKey(Material.ACACIA_PLANKS.toString())) {
      inputs.put(Material.OAK_DOOR.toString(), 1);
      inputs.put(Material.ACACIA_PLANKS.toString(), 3);
      products.put(Material.ACACIA_DOOR.toString(), 1);
    } else if (counts.containsKey("craftory:belt") && counts
        .containsKey(Material.REDSTONE.toString())) {
      inputs.put("craftory:belt", 1);
      inputs.put(Material.REDSTONE.toString(), 1);
      products.put("craftory:belt", 10);
    }
    if (inputs.size() == 0) {
      return null;
    }
    return new HashMap[]{inputs, products};
  }
}
