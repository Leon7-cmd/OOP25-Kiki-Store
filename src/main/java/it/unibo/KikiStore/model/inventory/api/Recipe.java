package it.unibo.KikiStore.model.inventory.api;

import java.util.List;

/**
 * Represents a potion recipe - the ingredients needed and the
 * potion it produces once unlocked.
 */
public interface Recipe {
    /**
     * Returns the ingredients required to craft this recipe.
     *
     * @return the list of required ingredients
     */
    List<Ingredient> getIngredients();

    /**
     * Returns the potion produced by this recipe.
     *
     * @return the resulting potion
     */
    Potion getPotion();

    /**
     * Checks whether the player has discovered this recipe.
     *
     * @return true if the recipe is unlocked
     */
    boolean isUnlocked();

    /**
     * Marks this recipe as unlocked.
     */
    void setUnlocked();
}
