package it.unibo.KikiStore.model.inventory.api;

import java.util.List;

/**
 * Catalog of all possible items in the game.
 * Provides the full list of all ingredients and potions
 * regardless of what the player currently owns.
 */
public interface GameCatalog {
    /**
     * @return all possible ingredients in the game
     */
    List<Ingredient> getAllIngredients();

    /**
     * @return all possible potions in the game
     */
    List<Potion> getAllPotions();

    
}
