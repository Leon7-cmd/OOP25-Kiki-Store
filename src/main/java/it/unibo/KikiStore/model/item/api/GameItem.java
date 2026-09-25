package it.unibo.KikiStore.model.item.api;

/**
 * Represents a generic item owned by the player - ingredients and
 * potions both build on this shared shape (name, sprite, quantity).
 */
public interface GameItem {
    /**
     * Returns the item's name.
     *
     * @return the item name
     */
    String getName();

    /**
     * Returns the sprite path used to draw this item.
     *
     * @return the image path
     */
    String getImagePath();

    /**
     * Returns how many of this item are currently owned.
     *
     * @return the quantity
     */
    int getQuantity();

    /**
     * Sets the item's name.
     *
     * @param name the new name
     */
    void setName(String name);

    /**
     * Sets the sprite path used to draw this item.
     *
     * @param imagePath the new image path
     */
    void setImagePath(String imagePath);

    /**
     * Sets how many of this item are currently owned.
     *
     * @param quantity the new quantity
     */
    void setQuantity(int quantity);
}
