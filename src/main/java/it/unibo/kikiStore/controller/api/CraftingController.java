package it.unibo.kikistore.controller.api;

import java.util.List;

import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Recipe;

/**
 * Handles the potion crafting logic - matching selected ingredients
 * against known recipes and updating the inventory accordingly.
 */
public interface CraftingController {
    /**
     * Attempts to craft a potion from the given ingredients. If they match
     * a known recipe, the resulting potion is added to the inventory and the
     * ingredients are consumed; otherwise a failed (black) potion is added.
     *
     * @param ingredients the selected ingredients
     */
    void craftPotion(List<Ingredient> ingredients);

    /**
     * Checks whether the given ingredients match a known recipe.
     *
     * @param ingredients the selected ingredients
     * @return true if a matching recipe exists
     */
    boolean canCraft(List<Ingredient> ingredients);

    /**
     * Returns the unlocked recipes the player currently has enough ingredients to craft.
     *
     * @return the list of craftable recipes
     */
    List<Recipe> getAvailableRecipes();

}
