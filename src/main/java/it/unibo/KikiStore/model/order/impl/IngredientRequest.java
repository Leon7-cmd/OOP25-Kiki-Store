package it.unibo.KikiStore.model.order.impl;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.order.api.CustomerRequest;

public class IngredientRequest implements CustomerRequest {

    private final Ingredient ingredient;

    public IngredientRequest(final Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public String getDialogue() {
        return "I brought this " + ingredient.getName() + ", can you make me a potion with it?";
    }
}
