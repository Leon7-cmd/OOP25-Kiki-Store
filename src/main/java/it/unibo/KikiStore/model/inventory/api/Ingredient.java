package it.unibo.KikiStore.model.inventory.api;

import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.model.item.api.PriceableItem;

/**
 * Represents a craftable ingredient — a {@link GameItem} with an added
 * type (e.g. plant, flower, mushroom, root) used to match recipes.
 */
public interface Ingredient extends GameItem, PriceableItem {
    /**
     * @return the ingredient's type (e.g. "plant", "flower", "mushroom", "root")
     */
    String getType();

    /**
     * @param type the ingredient's new type
     */
    void setType(String type);
}
