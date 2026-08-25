package it.unibo.KikiStore.model.memory.api;

/**
 * Represents a single card in the memory minigame.
 */
public interface MemoryCard {
    /**
     * Returns the sprite path shown when this card is revealed.
     *
     * @return the image path
     */
    String getImagePath();

    /**
     * Returns the identifier shared by this card and its matching pair.
     *
     * @return the pair id
     */
    int getPairId();

    /**
     * Returns the current visual state of the card.
     *
     * @return the card state
     */
    CardState getState();

    /**
     * Sets the current visual state of the card.
     *
     * @param state the new card state
     */
    void setState(CardState state);
}
