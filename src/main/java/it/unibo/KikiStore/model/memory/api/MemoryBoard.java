package it.unibo.KikiStore.model.memory.api;

import java.util.List;

/**
 * Represents the full grid of cards for a memory game session.
 */
public interface MemoryBoard {
    /**
     * Returns every card on the board, in their current shuffled order.
     *
     * @return the list of cards
     */
    List<MemoryCard> getCards();

    /**
     * Checks whether every card on the board has been matched and the board is complete.
     *
     * @return true if the board is complete
     */
    boolean isComplete();
}
