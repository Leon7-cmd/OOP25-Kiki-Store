package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple container for the player's owned ingredients and potions.
 * Quantity checks and crafting rules are in the controller layer.
 */
public final class InventoryImpl implements Inventory {
    private final List<Ingredient> ingredients;
    private final List<Potion> potions;

    /**
     * Creates an empty inventory.
     */
    public InventoryImpl() {
        this.ingredients = new ArrayList<>();
        this.potions = new ArrayList<>();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public List<Potion> getPotions() {
        return potions;
    }

    @Override
    public void addIngredient(final Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    @Override
    public void addPotion(final Potion potion) {
        potions.add(potion);
    }

    @Override
    public void removeIngredient(final Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    @Override
    public void removePotion(final Potion potion) {
        potions.remove(potion);
    }
}
