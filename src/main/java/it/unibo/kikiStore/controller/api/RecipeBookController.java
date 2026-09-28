package it.unibo.kikistore.controller.api;

import java.util.List;
import java.util.Optional;

import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Recipe;

/**
 * Provides access to the recipe book — querying, unlocking, and
 * searching recipes by ingredients, effect, or name.
 */
public interface RecipeBookController {
    /**
     * Returns every recipe, unlocked or not.
     *
     * @return the full list of recipes
     */
    List<Recipe> getAllRecipes();

    /**
     * Returns only the recipes the player has already unlocked.
     *
     * @return the list of unlocked recipes
     */
    List<Recipe> getUnlockedRecipes();

    /**
     * Marks the given recipe as unlocked.
     *
     * @param recipe the recipe to unlock
     */
    void unlockRecipe(Recipe recipe);

    /**
     * Returns the unlocked recipes the player currently has enough ingredients to
     * craft.
     *
     * @return the list of craftable recipes
     */
    List<Recipe> getCraftableRecipes();

    /**
     * Finds recipes whose potion effect matches or contains the given text.
     *
     * @param effect the effect to search for
     * @return the matching recipes
     */
    List<Recipe> findByEffect(String effect);

    /**
     * Finds the recipe that matches exactly the given set of ingredients.
     *
     * @param ingredients the ingredients to match
     * @return the matching recipe, or an empty Optional if none matches
     */
    Optional<Recipe> findByIngredients(List<Ingredient> ingredients);

    /**
     * Finds a recipe whose potion name matches or contains the given text.
     *
     * @param recipeName the name to search for
     * @return the matching recipe, or an empty Optional if none matches
     */
    Optional<Recipe> findByName(String recipeName);

    /**
     * Returns how many recipes the player has unlocked so far.
     *
     * @return the count of unlocked recipes
     */
    int getUnlockedCount();
}
