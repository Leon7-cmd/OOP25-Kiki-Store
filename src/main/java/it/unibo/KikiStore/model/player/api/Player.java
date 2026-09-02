package it.unibo.KikiStore.model.player.api;

import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import javafx.geometry.Rectangle2D;

/**
 * Logical model representing the playable character.
 */
public interface Player {

    /**
     * Attempts to move the player by the given directional delta.
     *
     * @param dx horizontal intent (-1 for left, +1 for right, 0 for none).
     * @param dy vertical intent (-1 for up, +1 for down, 0 for none).
     */
    void move(double dx, double dy);

    /**
     * @return current world X position in pixels.
     */
    double getX();

    /**
     * @return current world Y position in pixels.
     */
    double getY();

    /**
     * Sets the world X position.
     *
     * @param x new X coordinate.
     */
    void setX(double x);

    /**
     * Sets the world Y position.
     *
     * @param y new Y coordinate.
     */
    void setY(double y);

    /**
     * @return orientation string ("up", "down", "left", "right").
     */
    String getDirection();

    /**
     * @return animation state ("idle" or "walk").
     */
    String getState();

    /**
     * @return player's current currency.
     */
    int getMoney();

    /**
     * Adds the specified positive amount of currency.
     *
     * @param amount the money to add.
     */
    void addMoney(int amount);

    /**
     * Deducts currency if the player has sufficient funds.
     *
     * @param amount the money to spend.
     * @return true if successful, false if insufficient funds.
     */
    boolean spendMoney(int amount);

    /**
     * @return player's current energy units.
     */
    int getEnergy();

    /**
     * Restores energy without exceeding the upper limit.
     *
     * @param amount the energy units to restore.
     */
    void restoreEnergy(int amount);

    /**
     * Consumes energy if the player has enough available.
     *
     * @param amount the energy units to consume.
     * @return true if consumed successfully, false if insufficient energy.
     */
    boolean consumeEnergy(int amount);

    /**
     * @return the calculated bounding box used for collision detection.
     */
    Rectangle2D getHitbox();

    /**
     * Attaches or updates the collision handler for the current map.
     *
     * @param collisionHandler the collision engine to validate movement against.
     */
    void setCollisionHandler(CollisionHandler collisionHandler);
}
