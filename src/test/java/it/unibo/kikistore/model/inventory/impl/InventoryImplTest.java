package it.unibo.kikistore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InventoryImpl}.
 */
class InventoryImplTest {

    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String BASIL = "Basil";
    private static final String PLANT = "Plant";
    private static final String TEST_POTION = "Test Potion";
    private static final String TEST_SPRITE = "sprites/test";
    private static final String DESCRIPTION = "desc";
    private static final String EFFECT = "effect";

    /**
     * Verifies that a newly created inventory has no ingredients or potions.
     */
    @Test
    void newInventoryIsEmpty() {
        final InventoryImpl inventory = new InventoryImpl();

        assertTrue(inventory.getIngredients().isEmpty());
        assertTrue(inventory.getPotions().isEmpty());
    }

    /**
     * Verifies that adding and removing an ingredient updates the list correctly.
     */
    @Test
    void addAndRemoveIngredient() {
        final InventoryImpl inventory = new InventoryImpl();
        final IngredientImpl basil = new IngredientImpl(BASIL, BASIL_SPRITE, 1, PLANT);

        inventory.addIngredient(basil);
        assertEquals(1, inventory.getIngredients().size());

        inventory.removeIngredient(basil);
        assertTrue(inventory.getIngredients().isEmpty());
    }

    /**
     * Verifies that adding and removing a potion updates the inventory list
     * correctly.
     */
    @Test
    void addAndRemovePotion() {
        final InventoryImpl inventory = new InventoryImpl();
        final PotionImpl potion = new PotionImpl(TEST_POTION, TEST_SPRITE, 1, DESCRIPTION, EFFECT, false);

        inventory.addPotion(potion);
        assertEquals(1, inventory.getPotions().size());

        inventory.removePotion(potion);
        assertTrue(inventory.getPotions().isEmpty());
    }
}
