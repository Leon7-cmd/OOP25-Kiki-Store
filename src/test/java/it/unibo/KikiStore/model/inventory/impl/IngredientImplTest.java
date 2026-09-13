package it.unibo.KikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link IngredientImpl}. 
 */
class IngredientImplTest {
    
    /**
     * Verifies that the constructor sets every field correctly.
     */
    @Test
    void constructorSetsAllFields() {
        final IngredientImpl ingredient = new IngredientImpl("Basil", "sprites/basil", 3, "plant");

        assertEquals("Basil", ingredient.getName());
        assertEquals("sprites/basil", ingredient.getImagePath());
        assertEquals(3, ingredient.getQuantity());
        assertEquals("plant", ingredient.getType());
    }

    /**
     * Verifies that the setters update the stored values.
     */
    @Test
    void settersUpdateValues() {
        final IngredientImpl ingredient = new IngredientImpl("Basil", "sprites/basil", 3, "plant");

        ingredient.setType("flower");
        ingredient.setQuantity(6);

        assertEquals("flower", ingredient.getType());
        assertEquals(6, ingredient.getQuantity());
    }
}
