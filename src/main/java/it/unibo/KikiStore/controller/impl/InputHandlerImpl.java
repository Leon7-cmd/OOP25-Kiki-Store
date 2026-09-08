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
    private boolean cancel;//escape
    private boolean tab;//tab

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
            if (code == KeyCode.ESCAPE) {
                cancel = true;
            }
            if (code == KeyCode.TAB) {
                tab = true;
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
            if (code == KeyCode.ESCAPE) {
                cancel = false;
            }
            if (code == KeyCode.TAB) {
                tab = false;
            }
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

    @Override public boolean isCancel() {
        return cancel;
    }

    @Override public boolean isTab() {
        return tab;
    }
    //note: added cancel and tab methods to handle ESC and TAB inputs
    // In InputHandlerImpl:
    //note: added consumeAction() method to consume the action input after it has been processed, preventing repeated actions from a single key press.
    @Override
    public boolean consumeAction() {
        if (action) {
            action = false; // Consuma l'evento, si riattiverà solo alla prossima pressione fisica
            return true;
        }
        return false;
}
} 
