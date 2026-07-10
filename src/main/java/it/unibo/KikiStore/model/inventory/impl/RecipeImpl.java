package it.unibo.KikiStore.model.inventory.impl;
import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;

public class RecipeImpl implements Recipe{
    private final List<Ingredient> ingredients;
    private final Potion resultingPotion;
    private boolean isUnlocked = false;

    public RecipeImpl(List<Ingredient> ingredients, Potion resultingPotion, boolean isUnlocked) {
        this.ingredients = ingredients;
        this.resultingPotion = resultingPotion;
        this.isUnlocked = isUnlocked;
    }

    @Override public List<Ingredient> getIngredients() {
        return ingredients;
    } 

    @Override public Potion getPotion() {
        return resultingPotion;
    }

    @Override public boolean isUnlocked() {
        return isUnlocked;
    }

    @Override public void setUnlocked() {
        isUnlocked = true;
    }
}
