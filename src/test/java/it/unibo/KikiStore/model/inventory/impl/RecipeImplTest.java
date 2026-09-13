package it.unibo.KikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RecipeImpl}.
 */
class RecipeImplTest {

    /**
     * Verifies that a recipe created as locked starts with unlocked() as false,
     * and that its ingredients and potion match the constructor arguments.
     */
    @Test
    void recipeStartsLockedByDefault() {
        final IngredientImpl basil = new IngredientImpl("Basil", "sprites/basil", 0, "plant");
        final PotionImpl potion = new PotionImpl("Test Potion", "sprites/test", 0, "desc", "effect", false);
        final RecipeImpl recipe = new RecipeImpl(List.of(basil), potion, false);

        assertFalse(recipe.isUnlocked());
        assertEquals(potion, recipe.getPotion());
        assertEquals(1, recipe.getIngredients().size());
    }

    /**
     * Verifies that setUnlocked marks the recipe as unlocked.
     */
    @Test
    void setUnlockedMarksRecipeAsUnlocked() {
        final PotionImpl potion = new PotionImpl("Test Potion", "sprites/test", 0, "desc", "effect", false);
        final RecipeImpl recipe = new RecipeImpl(List.of(), potion, false);

        recipe.setUnlocked();

        assertTrue(recipe.isUnlocked());
    }
}
