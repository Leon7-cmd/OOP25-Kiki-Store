package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Implementation of the game state manager.
 * It acts as the intermediary component between the GameEngine and individual states (e.g., Menu, Gameplay, Pause).
 * Ensures that exactly one active state handles the update and rendering logic at any given time.
 */
public class GameStateManagerImpl implements GameStateManager {
    
    private GameState currentState;

    /**
     * Changes the active game state.
     * Automatically manages the lifecycle of the new state by calling its init() method.
     * 
     * @param state The new state to activate.
     */
    @Override
    public void setState(GameState state) {
        this.currentState = state;
        
        if (this.currentState != null) {
            this.currentState.init();
        }
    }

    @Override
    public GameState getCurrentState() {
        return this.currentState;
    }

    @Override
    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (currentState != null) {
            currentState.render(gc);
        }
    }
}