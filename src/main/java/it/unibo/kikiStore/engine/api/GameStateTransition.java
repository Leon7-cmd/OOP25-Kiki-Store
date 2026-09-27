package it.unibo.kikiStore.engine.api;

/**
 * Manages the Deque of GameStates.
 */
public interface GameStateTransition {

    /**
     * Pushes the newState on the top of the Deque.
     * 
     * @param newState The new game state to display
     */
    void pushState(GameState newState);

    /**
     * Pops a state off the Deque to unfreeze it.
     */
    void popState();

    /**
     * Clears the Deque of all states.
     */
    void clearStates();
}
