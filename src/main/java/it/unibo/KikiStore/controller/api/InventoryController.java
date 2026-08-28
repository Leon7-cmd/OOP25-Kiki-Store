package it.unibo.KikiStore.controller.api;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.List;

/**
 * Manages the player's inventory — adding, removing, and querying
 * ingredients and potions.
 */
public interface InventoryController {
    /**
     * Adds an ingredient to the inventory, creating it if not already present.
     *
     * @param name      the ingredient name
     * @param imagePath the sprite path for the ingredient
     * @param quantity  the quantity to add
     * @param type      the ingredient type (e.g. "plant", "mushroom")
     * @param price     the price of the ingredient in the store
     */
    void addIngredient(String name, String imagePath, int quantity, String type, int price);

    /**
     * Adds a potion to the inventory, creating it if not already present.
     *
     * @param name        the potion name
     * @param imagePath   the sprite path for the potion
     * @param quantity    the quantity to add
     * @param description the potion description
     * @param effect      the potion effect
     * @param isBlack     whether this is a failed/black potion
     */
    void addPotion(String name, String imagePath, int quantity, String description, String effect, boolean isBlack);

    /**
     * Removes a quantity of an ingredient from the inventory.
     *
     * @param name     the ingredient name
     * @param quantity the quantity to remove
     */
    void removeIngredient(String name, int quantity);

    /**
     * Removes a quantity of a potion from the inventory.
     *
     * @param name     the potion name
     * @param quantity the quantity to remove
     */
    void removePotion(String name, int quantity);

    /**
     * Checks whether the inventory contains at least one of the given ingredient.
     *
     * @param name the ingredient name
     * @return true if the ingredient is present
     */
    boolean hasIngredient(String name);

    /**
     * Checks whether the inventory contains at least one of the given potion.
     *
     * @param name the potion name
     * @return true if the potion is present
     */
    boolean hasPotion(String name);

    /**
     * Checks whether the inventory contains at least the given quantity of an
     * ingredient.
     *
     * @param name     the ingredient name
     * @param quantity the required quantity
     * @return true if enough of the ingredient is available
     */
    boolean hasEnoughIngredient(String name, int quantity);

    /**
     * Checks whether the inventory contains at least the given quantity of a
     * potion.
     *
     * @param name     the potion name
     * @param quantity the required quantity
     * @return true if enough of the potion is available
     */
    boolean hasEnoughPotion(String name, int quantity);

    /**
     * Returns the ingredients still missing to craft the given recipe.
     *
     * @param recipe the recipe to check
     * @return the list of missing ingredients
     */
    List<Ingredient> getMissingIngredients(Recipe recipe);

    /**
     * Checks whether the inventory has all the ingredients needed for the given
     * recipe.
     *
     * @param recipe the recipe to check
     * @return true if the recipe can be crafted
     */
    boolean canCraftPotion(Recipe recipe);

    /**
     * Returns the inventory.
     *
     * @return the inventory
     */
    Inventory getInventory();

    /**
     * Returns how many of the given ingredient are in the inventory.
     *
     * @param name the ingredient name
     * @return the ingredient quantity
     */
    int getIngredientQuantity(String name);

    /**
     * Returns how many of the given potion are in the inventory.
     *
     * @param name the potion name
     * @return the potion quantity
     */
    int getPotionQuantity(String name);

    /**
     * Checks whether the inventory has reached its maximum capacity.
     *
     * @return true if the inventory is full
     */
    boolean isFull();
}
