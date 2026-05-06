package it.unibo.KikiStore.model.item.impl;

import it.unibo.KikiStore.model.item.api.Item;
import javafx.geometry.Rectangle2D;

public class ItemImpl implements Item{
    private final String id;      
    private final double x, y;     
    private final double width, height; 
    private final boolean animated; 

    /**
     * @param id       Unique identifier for the item.
     * @param x        Initial X position in world coordinates.
     * @param y        Initial Y position in world coordinates.
     * @param width    The width of the item's sprite/hitbox.
     * @param height   The height of the item's sprite/hitbox.
     * @param animated Whether the item should be treated as an animated sprite.
     */
    public ItemImpl(String id, double x, double y, double width, double height, boolean animated) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animated = animated;
    }

    // --- View Getters ---
    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isAnimated() { return animated; }

    /**
     * Returns the collision area (Hitbox) of the object.
     * This is used by the collision engine to determine if Kiki or other entities 
     * are intersecting with this item.
     * 
     * @return A new Rectangle2D based on current position and dimensions.
     */
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, width, height);
    }
}
