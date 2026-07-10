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

    /**
     * @return the price of an ingredient by its name.
     */
    int getIngredientPrice(String ingredientName);

    /**
     * return the price of a potion by its name.
     */
    //int getPotionPrice(String potionName);// o forse no essendo da calcolare in base agli ingredienti, ma per ora lo lascio cosi
}