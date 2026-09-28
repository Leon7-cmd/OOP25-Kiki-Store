package it.unibo.kikistore.model.memory.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.kikistore.model.memory.api.CardState;
import it.unibo.kikistore.model.memory.api.MemoryBoard;
import it.unibo.kikistore.model.memory.api.MemoryCard;

/**
 * Default implementation of the memory board. Builds two cards for
 * each given image path (defined as a matching pair) and shuffles them.
 */
public final class MemoryBoardImpl implements MemoryBoard {
    private static final int CARDS_PER_PAIR = 2;

    private final List<MemoryCard> cards;

    /**
     * Builds a shuffled board with one matching pair per given image path.
     *
     * @param pairImagePaths the sprite path for each pair, one entry per pair
     */
    public MemoryBoardImpl(final List<String> pairImagePaths) {
        cards = new ArrayList<>();
        int pairId = 0;
        for (final String imagePath : pairImagePaths) {
            for (int i = 0; i < CARDS_PER_PAIR; i++) {
                cards.add(new MemoryCardImpl(imagePath, pairId));
            }
            pairId++;
        }
        Collections.shuffle(cards);
    }

    @Override
    public List<MemoryCard> getCards() {
        return Collections.unmodifiableList(cards);
    }

    @Override
    public boolean isComplete() {
        for (final MemoryCard card : cards) {
            if (card.getState() != CardState.MATCHED) {
                return false;
            }
        }
        return true;
    }
}
