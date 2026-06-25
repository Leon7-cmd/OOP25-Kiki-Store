package it.unibo.KikiStore.model.player.api;

import it.unibo.KikiStore.controller.api.InputHandler;

/**
 * Interface representing the player entity.
 * It manages the player's positioning, orientation, and logical updates.
 */
public interface Player {

    /**
     * @return the current X-coordinate of the player.
     */
    double getX();

    /**
     * Set a new value for the X-coordinate.
     * 
     * @param newX new X-coordinate value of the player.
     */
    void setX(double newX);

    /**
     * @return the current Y-coordinate of the player.
     */
    double getY();

    /**
     * Set a new value for the Y-coordinate.
     * 
     * @param newY new Y-coordinate value of the player.
     */
    void setY(double newY);

    /**
     * Retrieves the current facing direction of the player.
     * Common values: "up", "down", "left", "right".
     * 
     * @return a String representing the player's orientation.
     */
    String getDirection();

    /**
     * Retrieves the current animation or logical state of the player.
     * Common values: "idle", "walking", "interacting".
     * 
     * @return a String representing the player's current behavior.
     */
    String getState();

    /**
     * Updates the player's logic (movement, state transitions) based on current input.
     * This method is intended to be called once per frame by the Game Loop.
     * 
     * @param input the InputHandler providing the current state of the keys.
     */
    void update(InputHandler input); 
}
