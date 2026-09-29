package it.unibo.kikistore.engine.impl;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateManager;
import it.unibo.kikistore.engine.api.GameStateTransition;

/**
 * Implementation of the game state manager.
 * It acts as the intermediary component between the GameEngine and individual states (e.g., Menu, Gameplay, Pause).
 * Ensures that exactly one active state handles the update and rendering logic at any given time.
 */
public final class GameStateManagerImpl implements GameStateManager, GameStateTransition {

    //TRANSITION VARIABLES
    private static final double FADE_SPEED = 1.0 / 30.0;
    private static final double FADE_OUT_UPDATE_THRESHOLD = 0.9;
    private boolean isTransitioning;
    private double alpha;
    private boolean isPushAction;
    private int fadeDirection = 1;
    private GameState pendingState;

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
        pushState(state, true);
    }

    @Override
    public void pushState(final GameState newState, final boolean animated) {
        if (!animated || stateStack.isEmpty()) {
            applyPush(newState);
            return;
        }
        this.pendingState = newState;
        this.isPushAction = true;
        this.isTransitioning = true;
        this.fadeDirection = 1;
    }

    @Override
    public void popState(final boolean animated) {
        if (stateStack.isEmpty()) {
            return;
        }
        if (!animated) {
            applyPop();
            return;
        }
        this.isPushAction = false;
        this.isTransitioning = true;
        this.fadeDirection = 1;
    }

    private void applyPush(final GameState newState) {
        if (!stateStack.isEmpty()) {
            stateStack.peek().pause();
        }
        stateStack.push(newState);
        newState.init();
    }

    private void applyPop() {
        if (!stateStack.isEmpty()) {
            stateStack.pop();
        }
        if (!stateStack.isEmpty()) {
            stateStack.peek().resume();
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
                    applyPush(pendingState);
                    pendingState = null;
                } else if (!isPushAction) {
                    applyPop();
                }
            } else if (alpha <= 0.0) {
                alpha = 0.0;
                fadeDirection = 1;
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
            final Iterator<GameState> it = stateStack.descendingIterator();
            final int skip = Math.max(0, stateStack.size() - 2);
            for (int i = 0; i < skip; i++) {
                it.next();
            }
            while (it.hasNext()) {
                it.next().render(gc);
            }
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

    @Override
    public void clearStates() {
        stateStack.clear();
    }
}
