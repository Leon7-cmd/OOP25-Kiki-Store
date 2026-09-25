package it.unibo.KikiStore.model.inventory.api;

import it.unibo.KikiStore.model.item.api.GameItem;

/**
 * Represents a potion - a {@link GameItem} with a description and an
 * effect, plus a flag marking whether it's a failed brewing attempt
 * (a "black potion").
 */
public interface Potion extends GameItem {
    /**
     * @return the potion's flavor-text description
     */
    String getDescription();

    /**
     * @return the effect this potion provides (e.g. "sleep", "luck", "speed")
     */
    String getEffect();

    /**
     * @return true if this potion is a failed brewing attempt (black potion)
     */
    boolean isBlack();

    /**
     * @param description the potion's new description
     */
    void setDescription(String description);

    /**
     * @param effect the potion's new effect
     */
    void setEffect(String effect);

    /**
     * @param black true to mark this potion as a failed brewing attempt
     */
    void setBlack(boolean black);
}
