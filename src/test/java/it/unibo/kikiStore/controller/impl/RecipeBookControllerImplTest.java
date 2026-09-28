package it.unibo.kikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.api.Recipe;
import it.unibo.kikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.kikiStore.model.inventory.impl.RecipeBookImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RecipeBookControllerImpl}.
 */
class RecipeBookControllerImplTest {

    private static final String RECIPES_JSON = "textFiles/recipes.json";
    private static final String BASIL = "Basil";
    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String PLANT = "plant";
    private static final String FLOWER = "flower";
    private static final String WINDRUNNER = "Windrunner Potion";

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
    void recipesLoadedAndNoneUnlocked() {
        assertFalse(controller.getAllRecipes().isEmpty());
        assertTrue(controller.getUnlockedRecipes().isEmpty());
    }

    /**
     * Verifies that findByIngredients matches the correct recipe (Windrunner
     * Potion: basil, dandelion, sage) regardless of selection order.
     */
    @Test
    void findByIngredientsMatchesCorrectCombination() {
        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient dandelion = new IngredientImpl("Dandelion", "sprites/dandelion", 1, FLOWER);
        final Ingredient sage = new IngredientImpl("Sage", "sprites/sage", 1, PLANT);

        final Optional<Recipe> found = controller.findByIngredients(List.of(dandelion, basil, sage));

        assertTrue(found.isPresent());
        assertEquals(WINDRUNNER, found.get().getPotion().getName());
    }

    /**
     * Verifies that findByIngredients returns an empty Optional for a
     * combination that does not match any known recipe.
     */
    @Test
    void findByIngredientsIsEmptyForUnknownCombination() {
        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient chamomile = new IngredientImpl("Chamomile", "sprites/chamomile", 1, FLOWER);

        final Optional<Recipe> found = controller.findByIngredients(List.of(basil, chamomile));

        assertTrue(found.isEmpty());
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
        final Optional<Recipe> found = controller.findByName("windrunner");

        assertTrue(found.isPresent());
        assertEquals(WINDRUNNER, found.get().getPotion().getName());
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
