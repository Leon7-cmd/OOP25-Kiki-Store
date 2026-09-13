package it.unibo.KikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.PotionImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeImpl;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InventoryControllerImpl}.
 */
class InventoryControllerImplTest {

    /**
     * Verifies that adding a new ingredient makes it retrievable with the
     * given quantity.
     */
    @Test
    void addIngredientStoresQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();

        controller.addIngredient("Basil", "sprites/basil", 3, "plant");

        assertEquals(3, controller.getIngredientQuantity("Basil"));
        assertTrue(controller.hasIngredient("Basil"));
    }

    /**
     * Verifies that adding the same ingredient twice sums the quantities
     * instead of creating a duplicate entry.
     */
    @Test
    void addIngredientTwiceSumsQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();

        controller.addIngredient("Basil", "sprites/basil", 2, "plant");
        controller.addIngredient("Basil", "sprites/basil", 3, "plant");

        assertEquals(5, controller.getIngredientQuantity("Basil"));
    }

    /**
     * Verifies that removing ingredients reduces the stored quantity.
     */
    @Test
    void removeIngredientReducesQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient("Basil", "sprites/basil", 5, "plant");

        controller.removeIngredient("Basil", 2);

        assertEquals(3, controller.getIngredientQuantity("Basil"));
    }

    /**
     * Verifies that hasEnoughIngredient correctly compares against the
     * required quantity.
     */
    @Test
    void hasEnoughIngredientChecksQuantity() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient("Basil", "sprites/basil", 2, "plant");

        assertTrue(controller.hasEnoughIngredient("Basil", 2));
        assertFalse(controller.hasEnoughIngredient("Basil", 3));
    }

    /**
     * Verifies that canCraftPotion returns true only when every required
     * ingredient is available in sufficient quantity.
     */
    @Test
    void canCraftPotionChecksAllIngredients() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient("Basil", "sprites/basil", 1, "plant");
        controller.addIngredient("Sage", "sprites/sage", 1, "plant");
        controller.addIngredient("Dandelion", "sprites/dandelion", 1, "plant");

        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient sage = new IngredientImpl("Sage", "sprites/sage", 1, "plant");
        final Ingredient dandelion = new IngredientImpl("Dandelion", "sprites/dandelion", 1, "plant");
        final Ingredient chamomile = new IngredientImpl("Chamomile", "sprites/chamomile", 1, "plant");

        final RecipeImpl craftableRecipe = new RecipeImpl(List.of(basil, sage, dandelion),
                new PotionImpl("Test Potion", "sprites/test", 0, "desc", "effect", false), false);
        final RecipeImpl uncraftableRecipe = new RecipeImpl(List.of(basil, sage, chamomile),
                new PotionImpl("Test Potion 2", "sprites/test2", 0, "desc", "effect", false), false);

        assertTrue(controller.canCraftPotion(craftableRecipe));
        assertFalse(controller.canCraftPotion(uncraftableRecipe));
    }

    /**
     * Verifies that getMissingIngredients returns only the ingredients
     * not present in sufficient quantity.
     */
    @Test
    void getMissingIngredientsReturnsOnlyMissingOnes() {
        final InventoryControllerImpl controller = new InventoryControllerImpl();
        controller.addIngredient("Basil", "sprites/basil", 1, "plant");
        controller.addIngredient("Sage", "sprites/sage", 1, "plant");

        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient sage = new IngredientImpl("Sage", "sprites/sage", 1, "plant");
        final Ingredient dandelion = new IngredientImpl("Dandelion", "sprites/dandelion", 1, "plant");
        final RecipeImpl recipe = new RecipeImpl(List.of(basil, sage, dandelion),
                new PotionImpl("Test Potion", "sprites/test", 0, "desc", "effect", false), false);

        final List<Ingredient> missing = controller.getMissingIngredients(recipe);

        assertEquals(1, missing.size());
        assertEquals("Dandelion", missing.get(0).getName());
    }
}
