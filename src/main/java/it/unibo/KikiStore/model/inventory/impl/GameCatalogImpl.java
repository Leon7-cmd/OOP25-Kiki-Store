package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads all possible ingredients and potions from JSON files.
 * Returns unmodifiable lists to protect catalog integrity.
 */
public final class GameCatalogImpl implements GameCatalog {

    private final List<Ingredient> allIngredients;
    private final List<Potion> allPotions;

    /**
     * @param ingredientsJson path to ingredients.json in resources
     * @param potionsJson     path to potions.json in resources
     */
    public GameCatalogImpl(final String ingredientsJson, final String potionsJson) {
        allIngredients = new ArrayList<>();
        allPotions = new ArrayList<>();
        loadIngredients(ingredientsJson);
        loadPotions(potionsJson);
    }

    private void loadIngredients(final String path) {
        for (final JsonElement el : JsonResources.readArray(path)) {
            final JsonObject obj = el.getAsJsonObject();
            final String name = obj.get("name").getAsString();
            final String imagePath = obj.get("imagePath").getAsString();
            final String type = obj.get("type").getAsString();
            allIngredients.add(new IngredientImpl(name, imagePath, 0, type));
        }
    }

    private void loadPotions(final String path) {
        for (final JsonElement el : JsonResources.readArray(path)) {
            final JsonObject obj = el.getAsJsonObject();
            final String name = obj.get("name").getAsString();
            final String imagePath = obj.get("imagePath").getAsString();
            final String description = obj.get("description").getAsString();
            final String effect = obj.get("effect").getAsString();
            allPotions.add(new PotionImpl(name, imagePath, 0, description, effect, false));
        }
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return Collections.unmodifiableList(allIngredients);
    }

    @Override
    public List<Potion> getAllPotions() {
        return Collections.unmodifiableList(allPotions);
    }
}
