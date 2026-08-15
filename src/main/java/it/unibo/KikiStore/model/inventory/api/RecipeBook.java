package it.unibo.KikiStore.model.inventory.api;

import java.util.List;

/**
 * Holds the full set of known recipes, both locked and unlocked.
 */
public interface RecipeBook {
    /**
     * Returns every recipe, unlocked or not.
     *
     * @return the full list of recipes
     */
    List<Recipe> getRecipes();

    /**
     * Returns only the recipes the player has already unlocked.
     *
     * @return the list of unlocked recipes
     */
    List<Recipe> getUnlockedRecipes();

}
