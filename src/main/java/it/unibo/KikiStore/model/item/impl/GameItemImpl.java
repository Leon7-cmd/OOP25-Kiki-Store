package it.unibo.KikiStore.model.item.impl;

import it.unibo.KikiStore.model.item.api.GameItem;

/**
 * Base implementation of a game item - name, sprite path, and quantity.
 * Ingredients and potions both extend this class, adding their own
 * specific fields (type, effect, etc).
 */
public abstract class GameItemImpl implements GameItem {
    private String name;
    private String imagePath;
    private int quantity;

    /**
     * @param name      the item name
     * @param imagePath the sprite path for the item
     * @param quantity  the starting quantity
     */
    public GameItemImpl(final String name, final String imagePath, final int quantity) {
        this.name = name;
        this.imagePath = imagePath;
        this.quantity = quantity;
    }

    /**
     * Returns the item's name. Subclasses may override to add
     * validation or formatting, calling {@code super.getName()} for the base value.
     *
     * @return the item name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the sprite path used to draw this item. Subclasses may
     * override if the image path needs to be derived rather than stored directly.
     *
     * @return the image path
     */
    @Override
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Returns how many of this item are currently owned. Subclasses may
     * override to add bounds checking or trigger side effects on read.
     *
     * @return the quantity
     */
    @Override
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the item's name. Subclasses may override to add validation.
     *
     * @param name the new name
     */
    @Override public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the sprite path used to draw this item. Subclasses may
     * override to add validation.
     *
     * @param imagePath the new image path
     */
    @Override
    public void setImagePath(final String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Sets how many of this item are currently owned. Subclasses may
     * override to add bounds checking (e.g. preventing negative quantities).
     *
     * @param quantity the new quantity
     */
    @Override
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

}
