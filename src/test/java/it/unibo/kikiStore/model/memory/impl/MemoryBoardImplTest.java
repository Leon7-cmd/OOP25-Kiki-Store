package it.unibo.kikiStore.model.memory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.kikiStore.model.memory.api.CardState;
import it.unibo.kikiStore.model.memory.api.MemoryCard;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MemoryBoardImpl}.
 */
class MemoryBoardImplTest {

    private static final String BASIL_SPRITE = "sprites/basil";
    private static final String SAGE_SPRITE = "sprites/sage";
    private static final String DANDELION_SPRITE = "sprites/dandelion";

    /**
     * Verifies that the board creates exactly two cards per given path.
     */
    @Test
    void boardHasTwoCardsPerPair() {
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of(BASIL_SPRITE, SAGE_SPRITE));

        assertEquals(4, board.getCards().size());
    }

    /**
     * Verifies that every card has exactly one other card sharing its pair id.
     */
    @Test
    void everyCardHasExactlyOneMatchingPair() {
        final MemoryBoardImpl board = new MemoryBoardImpl(
                List.of(BASIL_SPRITE, SAGE_SPRITE, DANDELION_SPRITE));
        final List<MemoryCard> cards = board.getCards();

        for (final MemoryCard card : cards) {
            int matchCount = 0;
            for (final MemoryCard other : cards) {
                if (other.equals(card) && other.getPairId() == card.getPairId()) {
                    matchCount++;
                }
            }
            assertEquals(1, matchCount);
        }
    }

    /**
     * Verifies that a newly created board is not complete, since all
     * cards start hidden.
     */
    @Test
    void newBoardIsNotComplete() {
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of(BASIL_SPRITE, SAGE_SPRITE));

        assertFalse(board.isComplete());
    }

    /**
     * Verifies that the board is complete only once every card is matched.
     */
    @Test
    void boardIsCompleteWhenAllCardsAreMatched() {
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of(BASIL_SPRITE, SAGE_SPRITE));

        for (final MemoryCard card : board.getCards()) {
            card.setState(CardState.MATCHED);
        }

        assertTrue(board.isComplete());
    }
}
