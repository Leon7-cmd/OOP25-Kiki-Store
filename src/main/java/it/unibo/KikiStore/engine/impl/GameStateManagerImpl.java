package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implementation of the game state manager.
 * It acts as the intermediary component between the GameEngine and individual states (e.g., Menu, Gameplay, Pause).
 * Ensures that exactly one active state handles the update and rendering logic at any given time.
 */
public final class GameStateManagerImpl implements GameStateManager, GameStateTransition {

    //TRANSITION VARIABLES
    private static final double FADE_SPEED = 0.05;
    private static final double FADE_OUT_UPDATE_THRESHOLD = 0.9;
    private boolean isTransitioning;
    private double alpha;
    private int fadeDirection = 1;
    private GameState pendingState;
    private boolean isPushAction = true;

    private final Deque<GameState> stateStack = new ArrayDeque<>();

    /**
     * Changes the active game state.
     * Automatically manages the lifecycle of the new state by calling its init() method.
     * 
     * @param state The new state to activate.
     */
    @Override
    public void setState(final GameState state) {
        if (!stateStack.isEmpty()) {
            stateStack.clear();
        }
        pushState(state);
    }

    @Override
    public void pushState(final GameState newState) {
        if (stateStack.isEmpty()) {
            stateStack.push(newState);
            newState.init();
        } else {
            this.isTransitioning = true;
            this.pendingState = newState;
            this.fadeDirection = 1; 
            this.isPushAction = true;
        }
    }

    @Override
    public void popState() {
        if (stateStack.size() > 1) {
            this.isTransitioning = true;
            this.fadeDirection = 1;
            this.isPushAction = false;
        }
    }

    @Override
    public GameState getCurrentState() {
        return stateStack.peek();
    }

    @Override
    public void update() {
        if (isTransitioning) {
            alpha += FADE_SPEED * fadeDirection;
            if (alpha >= 1.0) {
                alpha = 1.0;
                fadeDirection = -1;
                if (isPushAction && pendingState != null) {
                    stateStack.push(pendingState);
                    pendingState.init();
                    pendingState = null;
                } else if (!isPushAction) {
                    stateStack.pop();
                }
            } else if (alpha <= 0.0) {
                alpha = 0.0;
                isTransitioning = false;
            }
        }

        if (!stateStack.isEmpty() && alpha < FADE_OUT_UPDATE_THRESHOLD) {
            stateStack.peek().update();
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        //1 Drawing the game
        if (!stateStack.isEmpty()) {
            stateStack.peek().render(gc);
        }

        //2 Drawing the black rectangle for the transition
        if (alpha > 0) {
            final double w = gc.getCanvas().getWidth();
            final double h = gc.getCanvas().getHeight();
            gc.save();
            gc.setGlobalAlpha(alpha);
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, w, h);
            gc.restore();
        }
    }
}
