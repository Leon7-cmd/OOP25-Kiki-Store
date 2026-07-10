package it.unibo.KikiStore.model.inventory.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;

/**
 * Loads all possible ingredients and potions from JSON files.
 * Returns unmodifiable lists to protect catalog integrity.
 */
public class GameCatalogImpl implements GameCatalog {

    private final List<Ingredient> allIngredients;
    private final List<Potion> allPotions;
    private final Map<String, Integer> ingredientPrices;

    /**
     * @param ingredientsJson path to ingredients.json in resources
     * @param potionsJson path to potions.json in resources
     */
    public GameCatalogImpl(final String ingredientsJson, final String potionsJson) {
        allIngredients = new ArrayList<>();
        allPotions = new ArrayList<>();
        ingredientPrices = new HashMap<>();

        loadIngredients(ingredientsJson);
        loadPotions(potionsJson);
    }

    private void loadIngredients(final String path) {
        final InputStream stream = getClass()
            .getClassLoader()
            .getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Ingredients JSON not found: " + path);
            return;
        }
        final JsonArray array = new Gson()
            .fromJson(new InputStreamReader(stream), JsonArray.class);
        for (final JsonElement el : array) {
            final JsonObject obj = el.getAsJsonObject();
            final String name      = obj.get("name").getAsString();
            final String imagePath = obj.get("imagePath").getAsString();
            final String type      = obj.get("type").getAsString();
            final int price = obj.get("price").getAsInt();

            ingredientPrices.put(name, price);

            allIngredients.add(new IngredientImpl(name, imagePath, 0, type));
        }
    }

    private void loadPotions(final String path) {
        final InputStream stream = getClass()
            .getClassLoader()
            .getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Potions JSON not found: " + path);
            return;
        }
        final JsonArray array = new Gson()
            .fromJson(new InputStreamReader(stream), JsonArray.class);
        for (final JsonElement el : array) {
            final JsonObject obj = el.getAsJsonObject();
            final String name        = obj.get("name").getAsString();
            final String imagePath   = obj.get("imagePath").getAsString();
            final String description = obj.get("description").getAsString();
            final String effect      = obj.get("effect").getAsString();
            allPotions.add(new PotionImpl(name, imagePath, 0, description, effect, false));
        }
    }

    @Override public List<Ingredient> getAllIngredients() {
        return Collections.unmodifiableList(allIngredients);
    }

    @Override public List<Potion> getAllPotions() {
        return Collections.unmodifiableList(allPotions);
    }

    @Override public int getIngredientPrice(final String ingredientName) {
        return ingredientPrices.getOrDefault(ingredientName, 0);
    }
}