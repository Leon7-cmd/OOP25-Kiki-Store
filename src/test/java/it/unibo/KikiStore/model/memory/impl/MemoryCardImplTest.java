package it.unibo.KikiStore.model.memory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unibo.KikiStore.model.memory.api.CardState;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MemoryCardImpl}.
 */
class MemoryCardImplTest {

    /**
     * Verifies that a newly created card starts hidden and has the
     * given image path and pair id.
     */
    @Test
    void constructorSetsFieldsAndStartsHidden() {
        final MemoryCardImpl card = new MemoryCardImpl("sprites/basil", 3);

        assertEquals("sprites/basil", card.getImagePath());
        assertEquals(3, card.getPairId());
        assertEquals(CardState.HIDDEN, card.getState());
    }

    /**
     * Verifies that setState updates the card's state.
     */
    @Test
    void setStateUpdatesState() {
        final MemoryCardImpl card = new MemoryCardImpl("sprites/basil", 0);

        card.setState(CardState.MATCHED);

        assertEquals(CardState.MATCHED, card.getState());
    }
}
