package it.unibo.KikiStore.model.inventory.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;

/**
 * Loads and holds all known recipes from a JSON file. Ingredients
 * inside each recipe are minimal placeholders (name only, quantity 0) -
 * used only for matching.
 */
public final class RecipeBookImpl implements RecipeBook {
    private final List<Recipe> allRecipes;
    private final GameCatalog catalog;
    // type isn't stored in the recipes JSON, only needed for the constructor
    private static final String PLACEHOLDER_TYPE = "plant";

    /**
     * @param jsonFile path to the recipes JSON file in resources
     * @param catalog the game catalog
     */
    public RecipeBookImpl(final String jsonFile, final GameCatalog catalog) {
        this.catalog = catalog;
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
                ingredients.add(resolveIngredient(ingName));
            }
            final Potion potion = new PotionImpl(name, id, 0, description, effect, false);
            final Recipe recipe = new RecipeImpl(ingredients, potion, false);
            allRecipes.add(recipe);
        }
    }

    /**
     * Risolve il nome ingrediente usato in recipes.json (es. "olive_leaf")
     * contro il catalogo reale, per ottenere il prezzo effettivo. Se non
     * trovato, ricade su un placeholder a prezzo 0 e avvisa in console -
     * utile per scovare disallineamenti tra recipes.json e ingredients.json.
     */
    
    private Ingredient resolveIngredient(final String recipeIngredientName) {
        final String normalized = recipeIngredientName.replace("_", " ").toLowerCase(Locale.ROOT);
        for (final Ingredient candidate : catalog.getAllIngredients()) {
            if (candidate.getName().toLowerCase(Locale.ROOT).equals(normalized)) {
                return candidate;
            }
        }
        System.err.println("Ingrediente non trovato nel catalogo: " + recipeIngredientName);
        return new IngredientImpl(recipeIngredientName, recipeIngredientName, 0, PLACEHOLDER_TYPE, 0);
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

    @Override public List<Recipe> findByEffect(final String effect) {
        final List<Recipe> matchingRecipes = new ArrayList<>();
        if (effect == null) {
            return matchingRecipes;
        }
        for (final Recipe recipe : allRecipes) {
            if (recipe.getPotion() != null && recipe.getPotion().getEffect() != null
                    && recipe.getPotion().getEffect().toLowerCase().contains(effect.toLowerCase())) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    @Override public Recipe findByIngredients(final List<Ingredient> ingredients) {
        if (ingredients == null) {
            return null;
        }
        for (final Recipe recipe : allRecipes) {
            if (recipe.getIngredients().containsAll(ingredients)
                    && recipe.getIngredients().size() == ingredients.size()) {
                return recipe;
            }
        }
        return null;
    }

    

    
}
