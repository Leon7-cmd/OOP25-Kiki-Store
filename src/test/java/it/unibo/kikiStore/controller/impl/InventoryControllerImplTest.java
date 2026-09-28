package it.unibo.kikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.kikiStore.model.inventory.impl.PotionImpl;
import it.unibo.kikiStore.model.inventory.impl.RecipeImpl;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InventoryControllerImpl}.
 */
class InventoryControllerImplTest {
    private static final String BASIL = "Basil";
    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String SAGE = "Sage";
    private static final String SAGE_SPRITE = "sprites/sage";
    private static final String DANDELION = "Dandelion";
    private static final String DANDELION_SPRITE = "sprites/dandelion";
    private static final String CHAMOMILE = "Chamomile";
    private static final String CHAMOMILE_SPRITE = "sprites/chamomile";
    private static final String PLANT = "plant";
    private static final String TEST_POTION = "Test Potion";
    private static final String TEST_SPRITE = "sprites/test";
    private static final String TEST_POTION_2 = "Test Potion 2";
    private static final String TEST_SPRITE_2 = "sprites/test2";
    private static final String DESCRIPTION = "desc";
    private static final String EFFECT = "effect";
    private static final int STOCK_QUANTITY = 5;

    /**
     * Verifies that adding a new ingredient makes it retrievable with the
     * given quantity.
     */
    @Test
    void addIngredientStoresQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();

        controller.addIngredient(BASIL, BASIL_SPRITE, 3, PLANT);

        assertEquals(3, controller.getIngredientQuantity(BASIL));
        assertTrue(controller.hasIngredient(BASIL));
    }

    /**
     * Verifies that adding the same ingredient twice sums the quantities
     * instead of creating a duplicate entry.
     */
    @Test
    void addIngredientTwiceSumsQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();

        controller.addIngredient(BASIL, BASIL_SPRITE, 2, PLANT);
        controller.addIngredient(BASIL, BASIL_SPRITE, 3, PLANT);

        assertEquals(STOCK_QUANTITY, controller.getIngredientQuantity(BASIL));
    }

    /**
     * Verifies that removing ingredients reduces the stored quantity.
     */
    @Test
    void removeIngredientReducesQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient(BASIL, BASIL_SPRITE, STOCK_QUANTITY, PLANT);

        controller.removeIngredient(BASIL, 2);

        assertEquals(3, controller.getIngredientQuantity(BASIL));
    }

    /**
     * Verifies that hasEnoughIngredient correctly compares against the
     * required quantity.
     */
    @Test
    void hasEnoughIngredientChecksQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient(BASIL, BASIL_SPRITE, 2, PLANT);

        assertTrue(controller.hasEnoughIngredient(BASIL, 2));
        assertFalse(controller.hasEnoughIngredient(BASIL, 3));
    }

    /**
     * Verifies that canCraftPotion returns true only when every required
     * ingredient is available in sufficient quantity.
     */
    @Test
    void canCraftPotionChecksAllIngredients() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient(BASIL, BASIL_SPRITE, 1, PLANT);
        controller.addIngredient(SAGE, SAGE_SPRITE, 1, PLANT);
        controller.addIngredient(DANDELION, DANDELION_SPRITE, 1, PLANT);

        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient sage = new IngredientImpl(SAGE, SAGE_SPRITE, 1, PLANT);
        final Ingredient dandelion = new IngredientImpl(DANDELION, DANDELION_SPRITE, 1, PLANT);
        final Ingredient chamomile = new IngredientImpl(CHAMOMILE, CHAMOMILE_SPRITE, 1, PLANT);

        final RecipeImpl craftableRecipe = new RecipeImpl(List.of(basil, sage, dandelion),
                new PotionImpl(TEST_POTION, TEST_SPRITE, 0, DESCRIPTION, EFFECT, false), false);
        final RecipeImpl uncraftableRecipe = new RecipeImpl(List.of(basil, sage, chamomile),
                new PotionImpl(TEST_POTION_2, TEST_SPRITE_2, 0, DESCRIPTION, EFFECT, false), false);

        assertTrue(controller.canCraftPotion(craftableRecipe));
        assertFalse(controller.canCraftPotion(uncraftableRecipe));
    }

    /**
     * Verifies that getMissingIngredients returns only the ingredients
     * not present in sufficient quantity.
     */
    @Test
    void missingIngredientsReturnsOnlyMissingOnes() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient(BASIL, BASIL_SPRITE, 1, PLANT);
        controller.addIngredient(SAGE, SAGE_SPRITE, 1, PLANT);

        final Ingredient basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);
        final Ingredient sage = new IngredientImpl(SAGE, SAGE_SPRITE, 1, PLANT);
        final Ingredient dandelion = new IngredientImpl(DANDELION, DANDELION_SPRITE, 1, PLANT);
        final RecipeImpl recipe = new RecipeImpl(List.of(basil, sage, dandelion),
                new PotionImpl(TEST_POTION, TEST_SPRITE, 0, DESCRIPTION, EFFECT, false), false);

        final List<Ingredient> missing = controller.getMissingIngredients(recipe);

        assertEquals(1, missing.size());
        assertEquals(DANDELION, missing.get(0).getName());
    }

    /**
     * Verifies that removing all units of a potion removes it from the inventory.
     */
    @Test
    void removePotionToZeroRemovesIt() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addPotion(TEST_POTION, TEST_SPRITE, 2, DESCRIPTION, EFFECT, false);

        controller.removePotion(TEST_POTION, 2);

        assertFalse(controller.hasPotion(TEST_POTION));
    }

    /**
     * Verifies that hasEnoughPotion compares against the owned quantity.
     */
    @Test
    void hasEnoughPotionChecksQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addPotion(TEST_POTION, TEST_SPRITE, 2, DESCRIPTION, EFFECT, false);

        assertTrue(controller.hasEnoughPotion(TEST_POTION, 2));
        assertFalse(controller.hasEnoughPotion(TEST_POTION, 3));
    }
}
