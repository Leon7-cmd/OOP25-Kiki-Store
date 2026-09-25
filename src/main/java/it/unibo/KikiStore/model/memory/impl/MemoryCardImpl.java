package it.unibo.KikiStore.model.memory.impl;

import it.unibo.KikiStore.model.memory.api.CardState;
import it.unibo.KikiStore.model.memory.api.MemoryCard;

/**
 * Default implementation of a memory card.
 */
public final class MemoryCardImpl implements MemoryCard {
    private final String imagePath;
    private final int pairId;
    private CardState state;

    /**
     * @param imagePath the sprite path shown when revealed
     * @param pairId    the identifier shared with this card's matching pair
     */
    public MemoryCardImpl(final String imagePath, final int pairId) {
        this.imagePath = imagePath;
        this.pairId = pairId;
        this.state = CardState.HIDDEN;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public int getPairId() {
        return pairId;
    }

    @Override
    public CardState getState() {
        return state;
    }

    @Override
    public void setState(final CardState state) {
        this.state = state;
    }
}
