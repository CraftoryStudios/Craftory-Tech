package tech.brettsaunders.craftory;

import java.util.HashMap;
import org.bukkit.Material;

public class MagicFusionRecipes {

  public static HashMap<Material, Integer>[] getRecipe(HashMap<Material, Integer> counts) {
    HashMap<Material, Integer> inputs = new HashMap<>();
    HashMap<Material, Integer> products = new HashMap<>();
    if (counts.containsKey(Material.GOLD_NUGGET) && counts.containsKey(Material.REDSTONE)) {
      inputs.put(Material.GOLD_NUGGET, 1);
      inputs.put(Material.REDSTONE, 1);
      products.put(Material.GLOWSTONE_DUST, 1);
    } else if(counts.containsKey(Material.OAK_DOOR) && counts.containsKey(Material.SPRUCE_PLANKS)) {
      inputs.put(Material.OAK_DOOR, 1);
      inputs.put(Material.SPRUCE_PLANKS, 3);
      products.put(Material.SPRUCE_DOOR, 1);
    } else if(counts.containsKey(Material.OAK_DOOR) && counts.containsKey(Material.JUNGLE_PLANKS)) {
      inputs.put(Material.OAK_DOOR, 1);
      inputs.put(Material.JUNGLE_PLANKS, 3);
      products.put(Material.JUNGLE_DOOR, 1);
    } else if(counts.containsKey(Material.OAK_DOOR) && counts.containsKey(Material.BIRCH_PLANKS)) {
      inputs.put(Material.OAK_DOOR, 1);
      inputs.put(Material.BIRCH_PLANKS, 3);
      products.put(Material.BIRCH_DOOR, 1);
    } else if(counts.containsKey(Material.OAK_DOOR) && counts.containsKey(Material.DARK_OAK_PLANKS)) {
      inputs.put(Material.OAK_DOOR, 1);
      inputs.put(Material.DARK_OAK_PLANKS, 3);
      products.put(Material.DARK_OAK_DOOR, 1);
    } else if(counts.containsKey(Material.OAK_DOOR) && counts.containsKey(Material.ACACIA_PLANKS)) {
      inputs.put(Material.OAK_DOOR, 1);
      inputs.put(Material.ACACIA_PLANKS, 3);
      products.put(Material.ACACIA_DOOR, 1);
    }
    if (inputs.size()==0) return null;
    return new HashMap[]{inputs, products};
  }
}
