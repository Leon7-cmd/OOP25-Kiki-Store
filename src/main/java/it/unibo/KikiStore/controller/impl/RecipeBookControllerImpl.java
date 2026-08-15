package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Provides access to the recipe book - querying, unlocking, and
 * searching recipes by ingredients, effect, or name.
 */
public final class RecipeBookControllerImpl implements RecipeBookController {
    private final RecipeBook recipeBook;
    private final InventoryController inventoryController;

    /**
     * @param recipeBook          the recipe book
     * @param inventoryController the inventory controller, used to check
     *                            craftability
     */
    public RecipeBookControllerImpl(final RecipeBook recipeBook, final InventoryController inventoryController) {
        this.recipeBook = recipeBook;
        this.inventoryController = inventoryController;
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeBook.getRecipes();
    }

    @Override
    public List<Recipe> getUnlockedRecipes() {
        return recipeBook.getUnlockedRecipes();
    }

    @Override
    public Recipe findByIngredients(final List<Ingredient> ingredients) {
        for (final Recipe recipe : recipeBook.getRecipes()) {
            if (matchesIngredients(recipe.getIngredients(), ingredients)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Checks whether two ingredient lists match by name, regardless of order.
     *
     * @param recipeIngredients the ingredients required by the recipe
     * @param selected          the ingredients chosen by the player
     * @return true if both lists contain the same ingredient names
     */
    private boolean matchesIngredients(final List<Ingredient> recipeIngredients, final List<Ingredient> selected) {
        if (recipeIngredients.size() != selected.size()) {
            return false;
        }
        for (final Ingredient required : recipeIngredients) {
            boolean found = false;
            for (final Ingredient chosen : selected) {
                if (required.getName().equalsIgnoreCase(chosen.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Recipe> findByEffect(final String effect) {
        final List<Recipe> matchingRecipes = new ArrayList<>();
        for (final Recipe recipe : recipeBook.getRecipes()) {
            if (recipe.getPotion().getEffect().toLowerCase(Locale.ROOT)
                    .contains(effect.toLowerCase())) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    @Override
    public void unlockRecipe(final Recipe recipe) {
        recipe.setUnlocked();
    }

    @Override
    public List<Recipe> getCraftableRecipes() {
        // TO-DO: duplicates CraftingControllerImpl.getAvailableRecipes(), maybe I can
        // remove one
        final List<Recipe> craftableRecipes = new ArrayList<>();

        for (final Recipe recipe : recipeBook.getUnlockedRecipes()) {
            if (inventoryController.canCraftPotion(recipe)) {
                craftableRecipes.add(recipe);
            }
        }

        return craftableRecipes;
    }

    @Override
    public Recipe findByName(final String recipeName) {
        for (final Recipe recipe : recipeBook.getRecipes()) {
            if (recipe.getPotion().getName().toLowerCase(Locale.ROOT)
                    .contains(recipeName.toLowerCase())) {
                return recipe;
            }
        }

        return null;
    }

    @Override
    public int getUnlockedCount() {
        return recipeBook.getUnlockedRecipes().size();
    }
}
