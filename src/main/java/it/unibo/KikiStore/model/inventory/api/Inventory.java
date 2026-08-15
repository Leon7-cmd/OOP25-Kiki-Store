package it.unibo.KikiStore.model.inventory.api;

import java.util.List;

/**
 * Represents the player's inventory — the raw storage of owned
 * ingredients and potions, adding and removing items.
 */
public interface Inventory {

    /**
     * @return the list of ingredients currently owned
     */
    List<Ingredient> getIngredients();

    /**
     * @return the list of potions currently owned
     */
    List<Potion> getPotions();

    /**
     * @param ingredient the ingredient to add to the inventory
     */
    void addIngredient(Ingredient ingredient);

    /**
     * @param potion the potion to add to the inventory
     */
    void addPotion(Potion potion);

    /**
     * @param ingredient the ingredient to remove from the inventory
     */
    void removeIngredient(Ingredient ingredient);

    /**
     * @param potion the potion to remove from the inventory
     */
    void removePotion(Potion potion);
}
