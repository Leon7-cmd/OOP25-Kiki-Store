package it.unibo.KikiStore.controller.api;

/**
 * Interface representing the player's input state.
 * It abstracts the physical input source into logical game actions.
 */
public interface InputHandler {
    boolean isUp();

    boolean isDown();

    boolean isLeft();

    boolean isRight();

    boolean isAction();
}
