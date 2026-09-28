package it.unibo.kikistore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.impl.IngredientImpl;
import it.unibo.kikistore.model.inventory.impl.RecipeBookImpl;

/**
 * Unit tests for {@link CraftingControllerImpl}.
 */
class CraftingControllerImplTest {
    private static final String BASIL = "Basil";
    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String SAGE = "Sage";
    private static final String SAGE_SPRITE = "sprites/sage";
    private static final String DANDELION = "Dandelion";
    private static final String DANDELION_SPRITE = "sprites/dandelion";
    private static final String CHAMOMILE = "Chamomile";
    private static final String CHAMOMILE_SPRITE = "sprites/chamomile";
    private static final String PLANT = "plant";
    private static final String FLOWER = "flower";
    private static final String WINDRUNNER = "Windrunner Potion";

    private static final String RECIPES_JSON = "textFiles/recipes.json";

    private InventoryControllerImpl inventoryController;
    private CraftingControllerImpl craftingController;

    /**
     * Builds a fresh set of controllers before each test, with the
     * ingredients for the Windrunner Potion recipe already in the inventory.
     */
    @BeforeEach
    void setUp() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);
        inventoryController = new InventoryControllerImpl();
        final RecipeBookControllerImpl recipeBookController = new RecipeBookControllerImpl(recipeBook,
                inventoryController);
        craftingController = new CraftingControllerImpl(inventoryController, recipeBookController);

        inventoryController.addIngredient(BASIL, BASIL_SPRITE, 1, PLANT);
        inventoryController.addIngredient(DANDELION, DANDELION_SPRITE, 1, FLOWER);
        inventoryController.addIngredient(SAGE, SAGE_SPRITE, 1, PLANT);
    }

    /**
     * Verifies that canCraft returns true for a known valid combination.
     */
    @Test
    void canCraftReturnsTrueForValidCombination() {
        final List<Ingredient> selected = correctIngredients();

        assertTrue(craftingController.canCraft(selected));
    }

    /**
     * Verifies that canCraft returns false for a combination matching no recipe.
     */
    @Test
    void canCraftReturnsFalseForInvalidCombination() {
        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient chamomile = new IngredientImpl(CHAMOMILE, CHAMOMILE_SPRITE, 1, FLOWER);

        assertFalse(craftingController.canCraft(List.of(basil, chamomile)));
    }

    /**
     * Verifies that crafting a valid combination adds the correct potion
     * to the inventory and consumes the ingredients used.
     */
    @Test
    void craftPotionWithValidCombinationAddsPotionAndConsumesIngredients() {
        craftingController.craftPotion(correctIngredients());

        assertTrue(inventoryController.hasPotion(WINDRUNNER));
        assertFalse(inventoryController.hasIngredient(BASIL));
    }

    /**
     * Verifies that crafting an invalid combination adds a black (failed)
     * potion instead of a real one.
     */
    @Test
    void craftPotionWithInvalidCombinationAddsBlackPotion() {
        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient chamomile = new IngredientImpl(CHAMOMILE, CHAMOMILE_SPRITE, 1, FLOWER);

        craftingController.craftPotion(List.of(basil, chamomile));

        assertTrue(inventoryController.hasPotion("Failed Potion"));
    }

    /**
     * @return the ingredients matching the Windrunner Potion recipe
     */
    private List<Ingredient> correctIngredients() {
        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient dandelion = new IngredientImpl(DANDELION, DANDELION_SPRITE, 1, FLOWER);
        final Ingredient sage = new IngredientImpl(SAGE, SAGE_SPRITE, 1, PLANT);
        return List.of(basil, dandelion, sage);
    }

    /**
     * Verifies that crafting consumes only one unit of each required
     * ingredient, not the whole stock.
     */
    @Test
    void craftPotionConsumesOnlyRequiredQuantity() {
        inventoryController.addIngredient(BASIL, BASIL_SPRITE, 4, PLANT);

        craftingController.craftPotion(correctIngredients());

        assertEquals(4, inventoryController.getIngredientQuantity(BASIL));
    }

    /**
     * Verifies that a crafted potion is added with quantity one.
     */
    @Test
    void craftPotionAddsOnePotion() {
        craftingController.craftPotion(correctIngredients());

        assertEquals(1, inventoryController.getPotionQuantity(WINDRUNNER));
    }

    /**
     * Verifies that an unlocked recipe is not available once its
     * ingredients have been used up.
     */
    @Test
    void unlockedRecipeIsNotAvailableWithoutIngredients() {
        craftingController.craftPotion(correctIngredients());

        assertTrue(craftingController.getAvailableRecipes().isEmpty());
    }
}
