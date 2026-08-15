package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Loads and holds all known recipes from a JSON file. Ingredients
 * inside each recipe are minimal placeholders (name only, quantity 0) -
 * used only for matching.
 */
public final class RecipeBookImpl implements RecipeBook {
    private final List<Recipe> allRecipes;
    // type isn't stored in the recipes JSON, only needed for the constructor
    private static final String PLACEHOLDER_TYPE = "plant";

    /**
     * @param jsonFile path to the recipes JSON file in resources
     */
    public RecipeBookImpl(final String jsonFile) {
        allRecipes = new ArrayList<>();
        loadFromJson(jsonFile);
    }

    /**
     * Reads the recipes JSON file and populates {@link #allRecipes}.
     *
     * @param jsonFile path to the recipes JSON file in resources
     */
    private void loadFromJson(final String jsonFile) {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(jsonFile);
        if (stream == null) {
            return;
        }
        final InputStreamReader reader = new InputStreamReader(stream);
        final JsonArray recipes = new Gson().fromJson(reader, JsonArray.class);

        for (final JsonElement entry : recipes) {
            final JsonObject recipeData = entry.getAsJsonObject();
            final String name = recipeData.get("name").getAsString();
            final String description = recipeData.get("description").getAsString();
            final String effect = recipeData.get("effect").getAsString();
            final String id = recipeData.get("id").getAsString();
            final JsonArray ingredientsArray = recipeData.get("ingredients").getAsJsonArray();
            final List<Ingredient> ingredients = new ArrayList<>();

            for (final JsonElement ing : ingredientsArray) {
                final String ingName = ing.getAsString();
                ingredients.add(new IngredientImpl(ingName, id, 0, PLACEHOLDER_TYPE));
            }
            final Potion potion = new PotionImpl(name, id, 0, description, effect, false);
            final Recipe recipe = new RecipeImpl(ingredients, potion, false);
            allRecipes.add(recipe);
        }
    }

    @Override
    public List<Recipe> getRecipes() {
        return allRecipes;
    }

    @Override
    public List<Recipe> getUnlockedRecipes() {
        final List<Recipe> unlockedRecipes = new ArrayList<>();
        for (final Recipe recipe : allRecipes) {
            if (recipe.isUnlocked()) {
                unlockedRecipes.add(recipe);
            }
        }
        return unlockedRecipes;
    }

}
