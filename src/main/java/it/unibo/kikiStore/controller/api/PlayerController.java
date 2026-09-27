package it.unibo.kikiStore.controller.api;

import it.unibo.kikiStore.model.map.impl.CollisionHandler;
import it.unibo.kikiStore.model.utility.BoundingBox;

/**
 * Controller responsible for translating user interactions into player model actions
 * and exposing essential state information to the views.
 */
public interface PlayerController {

    /**
     * Updates the player position based on active input.
     */
    void update();

    /**
     * @return the player's current X position.
     */
    double getX();

    /**
     * @return the player's current Y position.
     */
    double getY();

    /**
     * @return the player's current animation state.
     */
    String getState();

    /**
     * @return the player's current direction.
     */
    String getDirection();

    /**
     * @return the player's current hitbox for collision detection.
     */
    BoundingBox getHitbox();

    /**
     * @return the player's current energy.
     */
    int getEnergy();

    /**
     * @return the player's current money.
     */
    int getMoney();

    /**
     * Sets the player's position in the world.
     *
     * @param x the new X coordinate
     * @param y the new Y coordinate
     */
    void setPosition(double x, double y);

    /**
     * Sets the collision handler for the player.
     *
     * @param collisionHandler the CollisionHandler to use
     */
    void setCollisionHandler(CollisionHandler collisionHandler);

    /**
     * Adds the specified amount of money to the player's total.
     *
     * @param amount the amount of money to add
     */
    void addMoney(int amount);

    /**
     * Spends the specified amount of money if the player has enough.
     *
     * @param amount the amount of money to spend
     * @return true if the money was spent, false if insufficient funds
     */
    boolean spendMoney(int amount);

    /**
     * Restores the player's energy by the specified amount.
     *
     * @param amount the amount of energy to restore
     */
    void restoreEnergy(int amount);

    /**
     * Consumes the specified amount of energy from the player.
     *
     * @param amount the amount of energy to consume
     */
    void consumeEnergy(int amount);

    /**
     * Get the player's maximum energy.
     * 
     * @return the maximum energy value
     */
    int maxEnergy();
}
