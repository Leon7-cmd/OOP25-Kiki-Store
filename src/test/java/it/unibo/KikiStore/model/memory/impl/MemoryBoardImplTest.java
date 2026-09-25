package it.unibo.KikiStore.model.memory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.KikiStore.model.memory.api.CardState;
import it.unibo.KikiStore.model.memory.api.MemoryCard;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MemoryBoardImpl}.
 */
class MemoryBoardImplTest {

    /**
     * Verifies that the board creates exactly two cards per given path.
     */
    @Test
    void boardHasTwoCardsPerPair() {
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of("sprites/basil", "sprites/sage"));

        assertEquals(4, board.getCards().size());
    }

    /**
     * Verifies that every card has exactly one other card sharing its pair id.
     */
    @Test
    void everyCardHasExactlyOneMatchingPair() {
        final MemoryBoardImpl board = new MemoryBoardImpl(
                List.of("sprites/basil", "sprites/sage", "sprites/dandelion"));
        final List<MemoryCard> cards = board.getCards();

        for (final MemoryCard card : cards) {
            int matchCount = 0;
            for (final MemoryCard other : cards) {
                if (other != card && other.getPairId() == card.getPairId()) {
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
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of("sprites/basil", "sprites/sage"));

        assertFalse(board.isComplete());
    }

    /**
     * Verifies that the board is complete only once every card is matched.
     */
    @Test
    void boardIsCompleteWhenAllCardsAreMatched() {
        final MemoryBoardImpl board = new MemoryBoardImpl(List.of("sprites/basil", "sprites/sage"));

        for (final MemoryCard card : board.getCards()) {
            card.setState(CardState.MATCHED);
        }

        assertTrue(board.isComplete());
    }
}
