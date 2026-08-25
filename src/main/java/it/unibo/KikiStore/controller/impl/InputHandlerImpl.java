package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Manages all user inputs.
 */
public final class InputHandlerImpl implements InputHandler {

    // State variables: they keep track of which keys are currently held down
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean action;

    // Mouse click state: set on click, consumed (reset to false) by isMouseClicked()
    private boolean mouseClicked;
    private double mouseX;
    private double mouseY;

    /**
     * Constructor that attaches key listeners to the main application scene.
     * 
     * @param scene The JavaFX Scene to monitor for keyboard events.
     */
    public InputHandlerImpl(final Scene scene) {
        scene.setOnKeyPressed(event -> {
            final KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) { 
                up = true; 
            }
            if (code == KeyCode.S || code == KeyCode.DOWN) {
                down = true; 
            }
            if (code == KeyCode.A || code == KeyCode.LEFT) {
                left = true; 
            }
            if (code == KeyCode.D || code == KeyCode.RIGHT) { 
                right = true; 
            }
            if (code == KeyCode.E) {
                action = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            final KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) { 
                up = false; 
            }
            if (code == KeyCode.S || code == KeyCode.DOWN) { 
                down = false; 
            }
            if (code == KeyCode.A || code == KeyCode.LEFT) { 
                left = false; 
            }
            if (code == KeyCode.D || code == KeyCode.RIGHT) { 
                right = false; 
            }
            if (code == KeyCode.E) {
                action = false;
            }
        });

        scene.setOnMouseClicked(event -> {
            mouseClicked = true;
            mouseX = event.getX();
            mouseY = event.getY();
        });
    }

    @Override public boolean isUp() { 
        return up; 
    }

    @Override public boolean isDown() { 
        return down; 
    }

    @Override public boolean isLeft() { 
        return left; 
    }

    @Override public boolean isRight() { 
        return right; 
    }

    @Override public boolean isAction() { 
        return action; 
    }

    @Override
    public boolean isMouseClicked() {
        if (mouseClicked) {
            mouseClicked = false;
            return true;
        }
        return false;
    }

    @Override
    public double getMouseX() {
        return mouseX;
    }

    @Override
    public double getMouseY() {
        return mouseY;
    }
}
