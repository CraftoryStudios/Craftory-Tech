package tech.brettsaunders.craftory.api.recipes;

import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.Tags;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.utils.RecipeUtils;

import java.util.*;

public class ComplexityManager {
    private static Map<String, Integer> recipeTier;
    private Set<String> recipeResults = new HashSet<>();
    private Set<String> baseIngridents = new HashSet<>();
    private final JsonParser parser = new JsonParser();
    List<Recipe> unmappedRecipes = new ArrayList<>();

    @SneakyThrows
    public ComplexityManager() {
        recipeTier = new HashMap<>();
        for (Recipe recipe : RecipeUtils.getCraftingRecipes()) {
            recipeResults.add(recipe.getResult().getType().toString());
        }
        for (Recipe recipe : RecipeUtils.getFurnaceRecipes()) {
            baseIngridents.add(recipe.getResult().getType().toString());
        }
        for (Material crop : Tag.CROPS.getValues()) {
            baseIngridents.add(crop.toString());
        }

        calculateTiers();

        if (Utilities.config.getBoolean("autocrafting.output_complexity_values")) {
            FileConfiguration recipeTierOutput = new YamlConfiguration();
            for (String key : recipeTier.keySet()) {
                recipeTierOutput.set(key, recipeTier.get(key));
            }
            recipeTierOutput.save(Craftory.plugin.getDataFolder() + "/recipeTiers.yml");
        }
    }

    int getItemTier(String itemMaterialName) {
        if (recipeTier.containsKey(itemMaterialName)) {
            return recipeTier.get(itemMaterialName);
        }
        return -1;
    }

    private void calculateTiers() {
        for (Recipe recipe : RecipeUtils.getCraftingRecipes()) {
            getRecipeTier(recipe, true);
        }
        int startSize = unmappedRecipes.size();
        int endSize = 0;
        while (startSize != endSize) {
            startSize = unmappedRecipes.size();
            unmappedRecipes.removeIf(recipe -> getRecipeTier(recipe, false));
            endSize = unmappedRecipes.size();
        }
    }

    private boolean getRecipeTier(Recipe recipe, boolean addToUnmappedRecipes) {
        if (recipeTier.containsKey(recipe.getResult().getType().toString().toUpperCase())) {
            return true;
        }

        int ingredientsCombinedTier = -1;
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
            ingredientsCombinedTier = calculateChoicesComplexity(choices, recipe, addToUnmappedRecipes);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            ingredientsCombinedTier = calculateChoicesComplexity(shapelessRecipe.getChoiceList(), recipe, addToUnmappedRecipes);
        }
        if (ingredientsCombinedTier == -1) {
            return false;
        }
        String resultKey = recipe.getResult().getType().toString().toUpperCase();
        // Don't override default values
        if (!recipeTier.containsKey(resultKey)) {
            recipeTier.put(resultKey, ingredientsCombinedTier + 1);
        }
        return true;
    }

    private int calculateChoicesComplexity(List<RecipeChoice> choices, Recipe recipe, boolean addToUnmapped) {
        int complexityTotal = 0;
        for (RecipeChoice choice : choices) {
            if (choice == null) continue;

            boolean hasZeroIngrident = false;
            int ingridentComplexity = -1;
            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                for (Material material : materialChoice.getChoices()) {
                    if (!recipeResults.contains(material.toString()) || baseIngridents.contains(material.toString())) {
                        hasZeroIngrident = true;
                        continue;
                    }
                    int itemComplexity = getItemTier(material.toString());
                    if (itemComplexity > 0) {
                        ingridentComplexity = itemComplexity;
                        break;
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    String craftoryId = CustomItemManager.getCustomItemName(itemStack);
                    int itemComplexity = -1;
                    if (craftoryId != null) {
                        itemComplexity = getItemTier(craftoryId.toUpperCase());
                    } else {
                        if (!recipeResults.contains(itemStack.getType().toString()) || baseIngridents.contains(itemStack.getType().toString())) {
                            hasZeroIngrident = true;
                            continue;
                        }
                        itemComplexity = getItemTier(itemStack.getType().toString());
                    }
                    if (itemComplexity > 0) {
                        ingridentComplexity = itemComplexity;
                        break;
                    }
                }
            }

            if (ingridentComplexity == -1) {
                if (!hasZeroIngrident) {
                    if (addToUnmapped) {
                        unmappedRecipes.add(recipe);
                    }
                    return -1;
                } else {
                    complexityTotal += Math.min(0, complexityTotal);
                }
            } else {
                complexityTotal += ingridentComplexity;
            }

        }
        return complexityTotal;
    }


}
