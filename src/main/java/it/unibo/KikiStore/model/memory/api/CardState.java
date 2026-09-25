package it.unibo.KikiStore.model.memory.api;

/**
 * Represents the visual state of a single memory card.
 */
public enum CardState {
    /** Face down, not yet revealed by the player. */
    HIDDEN,
    /** Face up, currently shown to the player. */
    REVEALED,
    /** Matched with its pair - stays face up permanently. */
    MATCHED
}
