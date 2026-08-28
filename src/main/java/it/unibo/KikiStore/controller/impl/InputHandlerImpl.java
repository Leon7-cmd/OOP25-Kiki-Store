package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.view.hud.impl.HUDRenderer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * Manages all user inputs.
 */
public final class InputHandlerImpl implements InputHandler {

    private final HUDRenderer hudRenderer;
    private final GameStateManager gsm;
    private final GameStateTransition transition;
    private final Canvas canvas;

    // State variables: they keep track of which keys are currently held down
    private boolean up, down, left, right, action;

    /**
     * Constructor that attaches key listeners to the main application scene.
     * 
     * @param scene The JavaFX Scene to monitor for keyboard events.
     */
    public InputHandlerImpl(
        final Scene scene,
        final HUDRenderer hudRenderer,
        final GameStateManager gsm,
        final GameStateTransition transition,
        final Canvas canvas) {
            this.hudRenderer = hudRenderer;
            this.gsm = gsm;
            this.transition = transition;
            this.canvas = canvas;

        scene.setOnKeyPressed(event -> {
            final KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP)
                up = true;
            if (code == KeyCode.S || code == KeyCode.DOWN)
                down = true;
            if (code == KeyCode.A || code == KeyCode.LEFT)
                left = true;
            if (code == KeyCode.D || code == KeyCode.RIGHT)
                right = true;
            if (code == KeyCode.E)
                action = true;
        });

        scene.setOnKeyReleased(event -> {
            final KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP)
                up = false;
            if (code == KeyCode.S || code == KeyCode.DOWN)
                down = false;
            if (code == KeyCode.A || code == KeyCode.LEFT)
                left = false;
            if (code == KeyCode.D || code == KeyCode.RIGHT)
                right = false;
            if (code == KeyCode.E)
                action = false;
        });

// ...existing code...

        this.canvas.setOnMouseClicked(event -> {
            final double localX = event.getX();
            final double localY = event.getY();

            if (hudRenderer != null && hudRenderer.isMenuClicked(localX, localY)) {
                final var current = gsm.getCurrentState();
                gsm.setState(new it.unibo.KikiStore.engine.state.PauseMenuState(gsm, transition, current));
                return;
            }

            final var currentState = gsm.getCurrentState();
            if (currentState instanceof it.unibo.KikiStore.engine.state.PauseMenuState) {
                ((it.unibo.KikiStore.engine.state.PauseMenuState) currentState).handleMouseClick(localX, localY);
            }
        });
    }

    @Override public boolean isUp() { return up; }
    @Override public boolean isDown() { return down; }
    @Override public boolean isLeft() { return left; }
    @Override public boolean isRight() { return right; }
    @Override public boolean isAction() { return action; }
}