package it.unibo.KikiStore.engine.api;

import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a single state or "screen" within the game.
 * 
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
     * @param gc The JavaFX GraphicsContext to use for drawing on the Canvas.
     */
    void render(GraphicsContext gc); 
}