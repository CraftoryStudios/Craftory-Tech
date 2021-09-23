package tech.brettsaunders.craftory.utils.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeTree {
  private RecipeNode root;

  RecipeTree() {
    root = new RecipeNode();
  }

  public void insert(Recipe recipe) {
    RecipeNode current = root;
    List<ItemStack> choiceList = new ArrayList<>();

    if (recipe instanceof ShapedRecipe) {
      ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;

      Map<Character, ItemStack> choiceMap = shapedRecipe.getIngredientMap();
      String[] shape = Arrays.stream(shapedRecipe.getShape())
                             .map((line) -> line.split("(?!^)"))
                             .flatMap(Stream::of)
                             .toArray(String[]::new);
      choiceList = Arrays.stream(shape)
                                            .map((c) -> choiceMap.get(c.charAt(0)))
                                            .collect(Collectors.toList());
    } else if (recipe instanceof ShapelessRecipe) {
      ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
      choiceList = shapelessRecipe.getIngredientList().stream().filter(item -> item.getType() != Material.AIR).sorted(Comparator.comparing(o -> o.getType().getKey().toString(),
              Comparator.reverseOrder())).collect(Collectors.toList());
    }

    if (choiceList.size() == 6) {
      choiceList.add(0,new ItemStack(Material.AIR));
      choiceList.add(0,new ItemStack(Material.AIR));
      choiceList.add(0,new ItemStack(Material.AIR));
    }

    choiceList = reduceSearchSize(choiceList);

    for (ItemStack choice : choiceList) {
      // TODO allow tags
      String key;
      if (choice == null) {
        key = getItemStackKey(new ItemStack(Material.AIR));
      } else {
        key = getItemStackKey(choice);
      }

      current = current.getChildren().computeIfAbsent(key, c -> new RecipeNode());
    }
    current.setResult(true);
    current.setContent(recipe.getResult());
  }

  public Optional<ItemStack> find(ArrayList<ItemStack> ingredients) {
    RecipeNode current = root;
    for (int i = 0; i < ingredients.size(); i++) {
      if (Objects.isNull(ingredients.get(i))) {
        ingredients.set(i, new ItemStack(Material.AIR));
      }
    }

    // Shaped
    List<ItemStack> shapedIngredients = reduceSearchSize(new ArrayList<>(ingredients));
    for (int i = 0; i < shapedIngredients.size(); i++) {
      RecipeNode node = current.getChildren().get(getItemStackKey(shapedIngredients.get(i)));
      if (node == null) {
        break;
      }
      current = node;
    }
    if (current.isResult()) {
      return Optional.of(current.getContent());
    }

    // Shapeless
    List<ItemStack> shapelessIngredients =
        ingredients.stream().filter(item -> item.getType() != Material.AIR).sorted(Comparator.comparing(o -> o.getType().getKey().toString(),
        Comparator.reverseOrder())).collect(Collectors.toList());
    current = root;
    for (int i = 0; i < shapelessIngredients.size(); i++) {
      RecipeNode node = current.getChildren().get(getItemStackKey(shapelessIngredients.get(i)));
      if (node == null) {
        break;
      }
      current = node;
    }
    if (current.isResult()) {
      return Optional.of(current.getContent());
    }
    return Optional.empty();
  }

  private String getItemStackKey(ItemStack itemStack) {
    int modelData;
    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
      modelData = itemStack.getItemMeta().getCustomModelData();
    } else {
      modelData = 0;
    }
    return itemStack.getType().getKey() + ":" + modelData;
  }

  private List<ItemStack> reduceSearchSize(List<ItemStack> ingredients) {
    ingredients.replaceAll(t -> Objects.isNull(t) ? new ItemStack(Material.AIR) : t);
    if (ingredients.size() > 2 && ingredients.get(0).getType() == Material.AIR && ingredients.get(1).getType() == Material.AIR && ingredients.get(2).getType() == Material.AIR) {
      ingredients.remove(2);
      ingredients.remove(1);
      ingredients.remove(0);
    }
    if (ingredients.size() > 5 &&ingredients.get(3).getType() == Material.AIR && ingredients.get(4).getType() == Material.AIR && ingredients.get(5).getType() == Material.AIR) {
      ingredients.remove(5);
      ingredients.remove(4);
      ingredients.remove(3);
    }
    if (ingredients.size() > 8 &&ingredients.get(6).getType() == Material.AIR && ingredients.get(7).getType() == Material.AIR && ingredients.get(8).getType() == Material.AIR) {
      ingredients.remove(8);
      ingredients.remove(7);
      ingredients.remove(6);
    }
    return ingredients;
  }

}
