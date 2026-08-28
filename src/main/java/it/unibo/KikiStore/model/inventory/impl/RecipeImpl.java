package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import java.util.List;

/**
 * Concrete potion recipe - the ingredients needed and the potion
 * it produces, plus whether the player has discovered it yet.
 */
public final class RecipeImpl implements Recipe {
    private final List<Ingredient> ingredients;
    private final Potion resultingPotion;
    private boolean isUnlocked;

    /**
     * @param ingredients the ingredients required to craft this recipe
     * @param resultingPotion the potion produced by this recipe
     * @param isUnlocked whether the recipe starts already unlocked
     */
    public RecipeImpl(final List<Ingredient> ingredients, final Potion resultingPotion, final boolean isUnlocked) {
        this.ingredients = ingredients;
        this.resultingPotion = resultingPotion;
        this.isUnlocked = isUnlocked;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public Potion getPotion() {
        return resultingPotion;
    }

    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    @Override
    public void setUnlocked() {
        isUnlocked = true;
    }
}
