package it.unibo.kikiStore.model.memory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unibo.kikiStore.model.memory.api.CardState;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MemoryCardImpl}.
 */
class MemoryCardImplTest {

    private static final String BASIL_SPRITE = "sprites/basil";

    /**
     * Verifies that a newly created card starts hidden and has the
     * given image path and pair id.
     */
    @Test
    void constructorSetsFieldsAndStartsHidden() {
        final MemoryCardImpl card = new MemoryCardImpl(BASIL_SPRITE, 3);

        assertEquals(BASIL_SPRITE, card.getImagePath());
        assertEquals(3, card.getPairId());
        assertEquals(CardState.HIDDEN, card.getState());
    }

    /**
     * Verifies that setState updates the card's state.
     */
    @Test
    void setStateUpdatesState() {
        final MemoryCardImpl card = new MemoryCardImpl(BASIL_SPRITE, 0);

        card.setState(CardState.MATCHED);

        assertEquals(CardState.MATCHED, card.getState());
    }
}
