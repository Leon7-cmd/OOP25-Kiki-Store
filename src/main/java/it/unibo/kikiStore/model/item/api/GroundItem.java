package it.unibo.kikiStore.model.item.api;

import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.utility.BoundingBox;

/**
 * Defines a physical entity placed in the game world.
 */
public interface GroundItem {

    /**
     * Returns the path to the sprite.
     *
     * @return the sprite path
     */
    String getId();

    /**
     * Returns the horizontal coordinate in world space.
     *
     * @return the X coordinate in pixels
     */
    double getX();

    /**
     * Returns the vertical coordinate in world space.
     *
     * @return the Y coordinate in pixels
     */
    double getY();

    /**
     * Returns the width of the entity.
     *
     * @return the width in pixels
     */
    double getWidth();

    /**
     * Returns the height of the entity.
     *
     * @return the height in pixels
     */
    double getHeight();

    /**
     * Indicates if the entity is animated.
     *
     * @return true if animated, false otherwise
     */
    boolean isAnimated();

    /**
     * Returns the BoundingBox for pickup detection.
     *
     * @return a BoundingBox representing the item's hitbox
     */
    BoundingBox getHitbox();

    /**
     * Retrieves the ingredient to be transferred to the inventory.
     *
     * @return the Ingredient instance
     */
    Ingredient getItem();
}
