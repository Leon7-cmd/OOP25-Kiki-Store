package it.unibo.KikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PotionImpl}.
 */
class PotionImplTest {

    /**
     * Verifies that the constructor sets every field correctly.
     */
    @Test
    void constructorSetsAllFields() {
        final PotionImpl potion = new PotionImpl("Sleepy Potion", "sprites/sleepy", 1,
                "Helps you sleep", "sleep", false);

        assertEquals("Sleepy Potion", potion.getName());
        assertEquals("Helps you sleep", potion.getDescription());
        assertEquals("sleep", potion.getEffect());
        assertFalse(potion.isBlack());
    }

    /**
     * Verifies that setBlack correctly marks a potion as failed.
     */
    @Test
    void setBlackMarksFailedPotion() {
        final PotionImpl potion = new PotionImpl("Failed Potion", "sprites/black", 1,
                "A failed attempt", "none", false);

        potion.setBlack(true);

        assertTrue(potion.isBlack());
    }
}
