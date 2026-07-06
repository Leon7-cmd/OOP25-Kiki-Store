package it.unibo.KikiStore.model.item.impl;

import it.unibo.KikiStore.model.item.api.Item;
import javafx.geometry.Rectangle2D;

/**
 * Abstrct class for Items.
 */
public abstract class AbstractItemImpl implements Item {
    private final String id;
    private final String name;
    private int quantity;
    private final double x;
    private final double y;
    private final double width;
    private final double height; 
    private final boolean animated; 

    /**
     * @param quantity Quantity for the item.
     * @param name     Name for the item.
     * @param id       Unique identifier for the item.
     * @param x        Initial X position in world coordinates.
     * @param y        Initial Y position in world coordinates.
     * @param width    The width of the item's sprite/hitbox.
     * @param height   The height of the item's sprite/hitbox.
     * @param animated Whether the item should be treated as an animated sprite.
     */

    public AbstractItemImpl(
        final String id, 
        final double x, 
        final double y, 
        final double width, 
        final double height, 
        final boolean animated, 
        final String name, 
        final int quantity
    ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animated = animated;
        this.name = name;
        this.quantity = quantity;
    }

    // --- View Getters ---
    @Override public final int getQuantity() { 
        return quantity; 
    }

    @Override public final String getName() { 
        return name; 
    }

    @Override public final String getId() { 
        return id; 
    }

    @Override public final double getX() { 
        return x; 
    }

    @Override public final double getY() { 
        return y; 
    }

    @Override public final double getWidth() { 
        return width; 
    }

    @Override public final double getHeight() { 
        return height; 
    }

    @Override public final boolean isAnimated() { 
        return animated; 
    }

    // --- Setters ---
    @Override public final void setQuantity(final int quantity) { 
        this.quantity = quantity; 
    }

    /**
     * Returns the collision area (Hitbox) of the object.
     * This is used by the collision engine to determine if Kiki or other entities 
     * are intersecting with this item.
     * 
     * @return A new Rectangle2D based on current position and dimensions.
     */
    @Override public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, width, height);
    }
}
