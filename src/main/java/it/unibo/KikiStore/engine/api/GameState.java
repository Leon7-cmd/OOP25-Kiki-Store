package it.unibo.KikiStore.engine.api;

import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a single state or "screen" within the game.
 * Each class implementing this interface must define its own 
 * independent logic for initialization, updating, and rendering on screen.
 */
public interface GameState {

    /**
     * Initializes the state.
     * Called only once when this state becomes 
     * the active one (e.g., via the setState method of the GameStateManager).
     */
    void init(); 

    /**
     * Updates the internal logic of the state.
     * Invoked cyclically by the game engine's Game Loop.
     */
    void update(); 

    /**
     * Draws all visual elements of the state on the screen.
     * Invoked cyclically by the Game Loop immediately after update().
     * 
     * @param gc the JavaFX GraphicsContext to use for drawing on the Canvas.
     */
    void render(GraphicsContext gc);

    /**
     * Suspends the current state's execution when another state is pushed onto the stack.
     * Typically used to freeze inputs, cache entity coordinates, or halt local timers.
     */
    void pause();

    /**
     * Resumes the state's active lifecycle when it returns to the top of the stack after a pop.
     * Typically used to restore entity positions, re-inject map collision handlers, and reactivate logic.
     */
    void resume();
}
