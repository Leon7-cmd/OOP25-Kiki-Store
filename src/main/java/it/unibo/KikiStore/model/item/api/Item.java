package it.unibo.KikiStore.model.item.api;

import javafx.geometry.Rectangle2D;

/**
 * Defines all physical objects.
 */
public interface Item {

    /**
     * @return The unique identifier of the item (used for sprite lookup and logic).
     */
    String getId();
    
    /**
     * @return The X-coordinate of the item in the game world.
     */
    double getX();
    /**
     * @return The Y-coordinate of the item in the game world.
     */
    double getY();

    /**
     * @return The visual width of the item.
     */
    double getWidth();
    /**
     * @return The visual height of the item.
     */
    double getHeight();
    
    /**
     * Indicates whether the item requires frame-based animation 
     * 
     * @return true if the item is animated, false otherwise.
     */
    boolean isAnimated();

    /**
     * Returns the physical boundary used for collision detection and interaction.
     * 
     * @return A Rectangle2D representing the item's hitbox.
     */
    Rectangle2D getHitbox();
}
