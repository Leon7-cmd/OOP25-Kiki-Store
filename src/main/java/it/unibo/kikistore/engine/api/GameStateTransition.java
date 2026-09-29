package it.unibo.kikistore.engine.api;

/**
 * Manages the Deque of GameStates.
 */
public interface GameStateTransition {

    /**
     * Pushes the newState on the top of the Deque.
     * 
     * @param newState The new game state to display
     * @param animated true if the state need the in animation
     */
    void pushState(GameState newState, boolean animated);

    /**
     * Pops a state off the Deque to unfreeze it.
     * 
     * @param animated true if the state need the out animation
     */
    void popState(boolean animated);

    /**
     * Clears the Deque of all states.
     */
    void clearStates();
}
