package tech.brettsaunders.craftory.api.recipes;

import com.google.gson.JsonObject;
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
import tech.brettsaunders.craftory.utils.Log;
import tech.brettsaunders.craftory.utils.RecipeUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ComplexityManager {
    private static Map<String, Integer> complexities;
    private final JsonParser parser = new JsonParser();
    List<Recipe> unmappedRecipes = new ArrayList<>();

    @SneakyThrows
    public ComplexityManager() {
        complexities = new HashMap<>();
        getDefaultMappings();
        loadOverrides();
        generateComplexities();
        if (Utilities.config.getBoolean("autocrafting.output_complexity_values")) {
            FileConfiguration complexitiesOutput = new YamlConfiguration();
            for (String key : complexities.keySet()) {
                complexitiesOutput.set(key, complexities.get(key));
            }
            complexitiesOutput.save(Craftory.plugin.getDataFolder() + "/complexities.yml");
        }
        if (Utilities.config.getBoolean("general.debug")) {
            for (Recipe recipe : unmappedRecipes) {
                Log.debug("Missing mapping for " + recipe.getResult().getType());
            }
        }
    }

    public static int getComplexity(String itemId) {
        if (complexities.containsKey(itemId)) {
            return complexities.get(itemId);
        }
        return -1;
    }

    private void getDefaultMappings() {
        try {
            InputStream inputStream = Craftory.plugin.getResource("complexities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JsonObject jsonObject = parser.parse(json).getAsJsonObject();

            JsonObject mappings = jsonObject.get("values").getAsJsonObject();
            for (String key : mappings.keySet()) {

                // TAG
                if (key.startsWith("#")){
                    Tag<Material> tag = Tags.getTag("minecraft:" + key.substring(1).toLowerCase());
                    if (tag != null) {
                        for (Material material : tag.getValues()) {
                            complexities.put(material.toString().toUpperCase(), mappings.get(key).getAsInt());
                        }
                    }

                } else {
                    complexities.put(key.toUpperCase(), mappings.get(key).getAsInt());
                }
            }
        } catch (Exception ignored) {
            Log.debug("Failed to load default mappings");
        }
    }

    private void loadOverrides() {

    }

    private void generateComplexities() {
        for (Recipe furnanceRecipe : RecipeUtils.getFurnaceRecipes()) {
            generateRecipeComplexity(furnanceRecipe, true);
        }
        for (Recipe recipe : RecipeUtils.getCraftingRecipes()) {
            generateRecipeComplexity(recipe, true);
        }
        int startSize = unmappedRecipes.size();
        int endSize = 0;
        while (startSize != endSize) {
            startSize = unmappedRecipes.size();
            unmappedRecipes.removeIf(recipe -> generateRecipeComplexity(recipe, false));
            endSize = unmappedRecipes.size();
        }
    }

    private boolean generateRecipeComplexity(Recipe recipe, boolean addToUnmappedRecipes) {
        // Complexity already generated so prune this run
        if (complexities.containsKey(recipe.getResult().getType().toString().toUpperCase())) {
            return true;
        }

        int recipeComplexity = 0;
        if (recipe instanceof FurnaceRecipe furnaceRecipe) {
            recipeComplexity = getComplexity(furnaceRecipe.getInput().getType().toString().toUpperCase());
            if (recipeComplexity == -1) {
                if (addToUnmappedRecipes) {
                    unmappedRecipes.add(recipe);
                }
                return false;
            }
            // Add complexity of 1/8th of coal used for smelting onto the item
            recipeComplexity += 16;
        } else if (recipe instanceof ShapedRecipe shapedRecipe) {
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
            recipeComplexity = calculateChoicesComplexity(choices);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            recipeComplexity = calculateChoicesComplexity(shapelessRecipe.getChoiceList());
        }
        if (recipeComplexity == -1) {
            if (addToUnmappedRecipes) {
                unmappedRecipes.add(recipe);
            }
            return false;
        }
        String resultKey = recipe.getResult().getType().toString().toUpperCase();
        // Don't override default values
        if (!complexities.containsKey(resultKey)) {
            complexities.put(resultKey, (int) Math.ceil(recipeComplexity / recipe.getResult().getAmount() * 1.2) );
        }
        return true;
    }

    private int calculateChoicesComplexity(List<RecipeChoice> choices) {
        int complexityTotal = -1;
        for (RecipeChoice choice : choices) {
            if (choice == null) continue;

            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                for (Material material : materialChoice.getChoices()) {
                    int itemComplexity = getComplexity(material.toString());
                    if (itemComplexity > 0) {
                        complexityTotal += itemComplexity;
                        break;
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    String craftoryId = CustomItemManager.getCustomItemName(itemStack);
                    int itemComplexity = -1;
                    if (craftoryId != null) {
                        itemComplexity = getComplexity(craftoryId.toUpperCase());
                    } else {
                        itemComplexity = getComplexity(itemStack.getType().toString());
                    }
                    if (itemComplexity > 0) {
                        complexityTotal += itemComplexity;
                        break;
                    }
                }
            }

        }
        return complexityTotal;
    }


}
