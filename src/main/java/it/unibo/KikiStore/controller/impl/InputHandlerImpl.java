package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandlerImpl implements InputHandler {

    // State variables: they keep track of which keys are currently held down
    private boolean up, down, left, right, action;

    /**
     * Constructor that attaches key listeners to the main application scene.
     * 
     * @param scene The JavaFX Scene to monitor for keyboard events.
     */
    public InputHandlerImpl(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) up = true;
            if (code == KeyCode.S || code == KeyCode.DOWN) down = true;
            if (code == KeyCode.A || code == KeyCode.LEFT) left = true;
            if (code == KeyCode.D || code == KeyCode.RIGHT) right = true;
            if (code == KeyCode.E) action = true;
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) up = false;
            if (code == KeyCode.S || code == KeyCode.DOWN) down = false;
            if (code == KeyCode.A || code == KeyCode.LEFT) left = false;
            if (code == KeyCode.D || code == KeyCode.RIGHT) right = false;
            if (code == KeyCode.E) action = false;
        });
    }

    @Override public boolean isUp() { return up; }
    @Override public boolean isDown() { return down; }
    @Override public boolean isLeft() { return left; }
    @Override public boolean isRight() { return right; }
    @Override public boolean isAction() { return action; }
}