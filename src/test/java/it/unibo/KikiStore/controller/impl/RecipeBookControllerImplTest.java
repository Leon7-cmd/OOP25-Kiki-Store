package it.unibo.KikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RecipeBookControllerImpl}.
 */
class RecipeBookControllerImplTest {

    private static final String RECIPES_JSON = "textFiles/recipes.json";

    private RecipeBookControllerImpl controller;

    /**
     * Builds a fresh controller backed by the real recipes JSON before each test.
     */
    @BeforeEach
    void setUp() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);
        final InventoryControllerImpl inventoryController = new InventoryControllerImpl();
        controller = new RecipeBookControllerImpl(recipeBook, inventoryController);
    }

    /**
     * Verifies that all recipes are loaded and none is unlocked by default.
     */
    @Test
    void getAllRecipesAndUnlockedRecipes() {
        assertFalse(controller.getAllRecipes().isEmpty());
        assertTrue(controller.getUnlockedRecipes().isEmpty());
    }

    /**
     * Verifies that findByIngredients matches the correct recipe (Windrunner
     * Potion: basil, dandelion, sage) regardless of selection order.
     */
    @Test
    void findByIngredientsMatchesCorrectCombination() {
        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient dandelion = new IngredientImpl("Dandelion", "sprites/dandelion", 1, "flower");
        final Ingredient sage = new IngredientImpl("Sage", "sprites/sage", 1, "plant");

        final Recipe found = controller.findByIngredients(List.of(dandelion, basil, sage));

        assertNotNull(found);
        assertEquals("Windrunner Potion", found.getPotion().getName());
    }

    /**
     * Verifies that findByIngredients returns null for a combination that
     * does not match any known recipe.
     */
    @Test
    void findByIngredientsReturnsNullForUnknownCombination() {
        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient chamomile = new IngredientImpl("Chamomile", "sprites/chamomile", 1, "flower");

        final Recipe found = controller.findByIngredients(List.of(basil, chamomile));

        assertNull(found);
    }

    /**
     * Verifies that unlockRecipe moves a recipe into the unlocked list and
     * updates the unlocked count.
     */
    @Test
    void unlockRecipeUpdatesUnlockedListAndCount() {
        final Recipe recipe = controller.getAllRecipes().get(0);

        controller.unlockRecipe(recipe);

        assertTrue(controller.getUnlockedRecipes().contains(recipe));
        assertEquals(1, controller.getUnlockedCount());
    }

    /**
     * Verifies that findByName finds a recipe by a partial, case-insensitive match.
     */
    @Test
    void findByNameMatchesPartialName() {
        final Recipe found = controller.findByName("windrunner");

        assertNotNull(found);
        assertEquals("Windrunner Potion", found.getPotion().getName());
    }

    /**
     * Verifies that findByEffect finds recipes whose potion effect matches.
     */
    @Test
    void findByEffectMatchesKnownEffect() {
        final List<Recipe> found = controller.findByEffect("speed");

        assertFalse(found.isEmpty());
    }
}
