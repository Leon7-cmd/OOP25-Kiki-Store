package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implementation of the game state manager.
 * It acts as the intermediary component between the GameEngine and individual states (e.g., Menu, Gameplay, Pause).
 * Ensures that exactly one active state handles the update and rendering logic at any given time.
 */
public class GameStateManagerImpl implements GameStateManager, GameStateTransition {
    
    private final Deque<GameState> stateStack = new ArrayDeque<>();

    /**
     * Changes the active game state.
     * Automatically manages the lifecycle of the new state by calling its init() method.
     * 
     * @param state The new state to activate.
     */
    @Override
    public void setState(GameState state) {
        if (!stateStack.isEmpty()) {
            stateStack.clear();
        }
        pushState(state);
    }

    @Override
    public void pushState(GameState newState){
        if(newState != null){
            stateStack.push(newState);
            newState.init();
        }
    }

    @Override
    public void popState(){
        if(stateStack.size() > 1){
            stateStack.pop();
        }
    }

    @Override
    public GameState getCurrentState() {
        return stateStack.peek();
    }

    @Override
    public void update() {
        if (!stateStack.isEmpty()) {
            stateStack.peek().update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!stateStack.isEmpty()) {
            stateStack.peek().render(gc);
        }
    }
}