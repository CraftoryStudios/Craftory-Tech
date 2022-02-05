package tech.brettsaunders.craftory.api.recipes;

import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.RecipeUtils;

import java.util.*;

public class ComplexityManager {
    private final Map<String, Integer> itemTiers;

    List<Recipe> unmappedRecipes = new ArrayList<>();

    @SneakyThrows
    public ComplexityManager() {
        itemTiers = new HashMap<>();
        itemTiers.put("IRON_INGOT", 1);
        itemTiers.put("GOLD_INGOT", 2);
        itemTiers.put("DIAMOND", 3);
        itemTiers.put("EMERALD", 4);
        itemTiers.put("NETHERITE_SCRAP", 5);
        itemTiers.put("NETHERITE_INGOT", 5);

        calculateTiers();

        if (Utilities.config.getBoolean("autocrafting.output_complexity_values")) {
            FileConfiguration recipeTierOutput = new YamlConfiguration();
            for (String key : itemTiers.keySet()) {
                recipeTierOutput.set(key, itemTiers.get(key));
            }
            recipeTierOutput.save(Craftory.plugin.getDataFolder() + "/itemTiers.yml");
        }
    }

    public int getItemTier(String itemMaterialName) {
        if (itemTiers.containsKey(itemMaterialName)) {
            return itemTiers.get(itemMaterialName);
        }
        return 0;
    }

    private void calculateTiers() {
        for (Recipe recipe : RecipeUtils.getCraftingRecipes()) {
            calculateItemTier(recipe, true);
        }
        int startSize = unmappedRecipes.size();
        int endSize = 0;
        while (startSize != endSize) {
            startSize = unmappedRecipes.size();
            unmappedRecipes.removeIf(recipe -> calculateItemTier(recipe, false));
            endSize = unmappedRecipes.size();
        }
    }

    private boolean calculateItemTier(Recipe recipe, boolean addToUnmappedRecipes) {
        String resultKey = recipe.getResult().getType().toString().toUpperCase();
        // If item mapped, then skip calculation
        if (itemTiers.containsKey(resultKey)) {
            return true;
        }

        int itemTier = -1;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            // Calculate amounts of each ingredient
            Map<Character, Integer> ingredientCounts = new HashMap<>();
            for (String row : shapedRecipe.getShape()) {
                for (char c : row.toCharArray()) {
                    ingredientCounts.put(c, ingredientCounts.getOrDefault(c, 0) + 1);
                }
            }
            // Generate choice list with the correct amount of each ingredient
            List<RecipeChoice> choices = new ArrayList<>();
            for (Map.Entry<Character, RecipeChoice> entry : shapedRecipe.getChoiceMap().entrySet()) {
                for (int i = 0; i < ingredientCounts.get(entry.getKey()); i++) {
                    choices.add(entry.getValue());
                }
            }
            itemTier = calculateChoicesComplexity(choices, recipe, addToUnmappedRecipes);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            itemTier = calculateChoicesComplexity(shapelessRecipe.getChoiceList(), recipe, addToUnmappedRecipes);
        }
        if (itemTier == -1) {
            unmappedRecipes.add(recipe);
            return false;
        }
        itemTiers.put(resultKey, itemTier);
        return true;
    }

    private int calculateChoicesComplexity(List<RecipeChoice> choices, Recipe recipe, boolean addToUnmapped) {
        Set<Integer> ingridentTiers = new HashSet<>();
        for (RecipeChoice choice : choices) {
            if (choice == null) continue;

            int ingridentTier = -1;
            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                for (Material material : materialChoice.getChoices()) {
                    ingridentTier = getItemTier(material.toString());
                    if (ingridentTier == -1) {
                        return -1;
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    String craftoryId = CustomItemManager.getCustomItemName(itemStack);
                    if (craftoryId != null) {
                        ingridentTier = getItemTier(craftoryId.toUpperCase());
                    } else {
                        ingridentTier = getItemTier(itemStack.getType().toString());
                    }
                    if (ingridentTier == -1) {
                        return -1;
                    }
                }
            }
            ingridentTiers.add(ingridentTier);
        }
        return Collections.max(ingridentTiers);
    }


}
