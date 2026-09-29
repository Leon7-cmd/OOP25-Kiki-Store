package it.unibo.kikistore.controller.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Recipe;
import it.unibo.kikistore.model.inventory.api.RecipeBook;

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
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Controllers are injected on purpose and shared with the rest of the game"
    )
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
    public Optional<Recipe> findByIngredients(final List<Ingredient> ingredients) {
        for (final Recipe recipe : recipeBook.getRecipes()) {
            if (matchesIngredients(recipe.getIngredients(), ingredients)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
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
        final String searched = effect.toLowerCase(Locale.ROOT);
        return recipeBook.getRecipes().stream()
                .filter(r -> r.getPotion().getEffect().toLowerCase(Locale.ROOT).contains(searched))
                .toList();
    }

    @Override
    public void unlockRecipe(final Recipe recipe) {
        recipe.setUnlocked();
    }

    @Override
    public List<Recipe> getCraftableRecipes() {
        return recipeBook.getUnlockedRecipes().stream()
                .filter(inventoryController::canCraftPotion)
                .toList();
    }

    @Override
    public Optional<Recipe> findByName(final String recipeName) {
        for (final Recipe recipe : recipeBook.getRecipes()) {
            if (recipe.getPotion().getName().toLowerCase(Locale.ROOT)
                    .contains(recipeName.toLowerCase(Locale.ROOT))) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public int getUnlockedCount() {
        return recipeBook.getUnlockedRecipes().size();
    }
}
