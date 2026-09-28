package it.unibo.kikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link IngredientImpl}.
 */
class IngredientImplTest {
    private static final String BASIL = "Basil";
    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String PLANT = "plant";
    private static final String FLOWER = "flower";
    private static final int NEW_QUANTITY = 6;

    /**
     * Verifies that the constructor sets every field correctly.
     */
    @Test
    void constructorSetsAllFields() {
        final IngredientImpl ingredient = new IngredientImpl(BASIL, BASIL_SPRITE, 3, PLANT);

        assertEquals(BASIL, ingredient.getName());
        assertEquals(BASIL_SPRITE, ingredient.getImagePath());
        assertEquals(3, ingredient.getQuantity());
        assertEquals(PLANT, ingredient.getType());
    }

    /**
     * Verifies that the setters update the stored values.
     */
    @Test
    void settersUpdateValues() {
        final IngredientImpl ingredient = new IngredientImpl(BASIL, BASIL_SPRITE, 3, PLANT);

        ingredient.setType(FLOWER);
        ingredient.setQuantity(NEW_QUANTITY);

        assertEquals(FLOWER, ingredient.getType());
        assertEquals(NEW_QUANTITY, ingredient.getQuantity());
    }
}
