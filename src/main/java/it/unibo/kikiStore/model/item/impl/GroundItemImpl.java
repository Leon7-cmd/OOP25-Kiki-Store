package it.unibo.kikiStore.model.item.impl;

import java.util.Objects;

import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.item.api.GroundItem;
import it.unibo.kikiStore.model.utility.BoundingBox;

/**
 * Standard implementation of a {@link GroundItem}.
 */
public final class GroundItemImpl implements GroundItem {

    private final String id;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final boolean animated;
    private final Ingredient item;

    /**
     * Constructs a new ground item entity in the game world.
     *
     * @param id       the asset identifier used for sprite lookups
     * @param x        the world X coordinate in pixels
     * @param y        the world Y coordinate in pixels
     * @param width    the hitbox and render width in pixels
     * @param height   the hitbox and render height in pixels
     * @param animated true if the sprite uses frame-based animation
     * @param item     the logical item payload to wrap
     */
    public GroundItemImpl(
        final String id,
        final double x,
        final double y,
        final double width,
        final double height,
        final boolean animated,
        final Ingredient item
    ) {
        this.id = Objects.requireNonNull(id, "Asset ID cannot be null");
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animated = animated;
        this.item = Objects.requireNonNull(item, "Item payload cannot be null");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public boolean isAnimated() {
        return animated;
    }

    @Override
    public BoundingBox getHitbox() {
        return new BoundingBox(x, y, width, height);
    }

    @Override
    public Ingredient getItem() {
        return item;
    }
}
