package it.unibo.KikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RecipeBookImpl}.
 */
class RecipeBookImplTest {

    private static final String RECIPES_JSON = "textFiles/recipes.json";

    /**
     * Verifies that recipes are loaded from the JSON file
     */
    @Test
    void loadsRecipesFromJson() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);

        assertFalse(recipeBook.getRecipes().isEmpty());
    }

    /**
     * Verifies that every recipe starts locked when it is loaded.
     */
    @Test
    void newlyLoadedRecipesAreAllLocked() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);

        assertTrue(recipeBook.getUnlockedRecipes().isEmpty());
    }

    /**
     * Verifies that unlocking a recipe makes it appear in the unlocked list.
     */
    @Test
    void unlockingARecipeMovesItToUnlockedList() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);

        recipeBook.getRecipes().get(0).setUnlocked();

        assertFalse(recipeBook.getUnlockedRecipes().isEmpty());
    }
}
