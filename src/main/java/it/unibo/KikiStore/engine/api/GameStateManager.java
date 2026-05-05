package it.unibo.KikiStore.engine.api;

import javafx.scene.canvas.GraphicsContext;

/**
 * Manages the lifecycle and the transition between various game states.
 * Ensures that the main cycles of the Game Loop (updating and rendering)
 * are correctly delegated to the currently active state.
 */
public interface GameStateManager {
    
    /**
     * Sets and activates a new game state.
     * 
     * @param state The new state to set as current.
     */
    void setState(GameState state);

    /**
     * Returns the currently executing game state.
     * 
     * @return The instance of the active GameState.
     */
    GameState getCurrentState();
    
    /**
     * Delegates the logic update cycle to the current state.
     */
    void update();

    /**
     * Delegates the drawing cycle (render) to the current state.
     * 
     * @param gc The GraphicsContext used to draw on the Canvas.
     */
    void render(GraphicsContext gc);
}