package tech.brettsaunders.craftory.utils.recipes;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class RecipeNode {
  private Map<String, RecipeNode> children = new HashMap<>();
  private ItemStack content;
  private boolean isResult;
}
