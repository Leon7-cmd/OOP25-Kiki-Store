package it.unibo.kikistore.controller.impl;

import java.util.List;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.CraftingController;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Potion;
import it.unibo.kikistore.model.inventory.api.Recipe;

/**
 * Handles the potion crafting logic - matching selected ingredients
 * against known recipes and updating the inventory accordingly.
 */
public final class CraftingControllerImpl implements CraftingController {
    private static final String BLACK_POTION_NAME = "Failed Potion";
    private static final String BLACK_POTION_PATH = "sprites/potions/black_potion";

    private final InventoryController inventoryController;
    private final RecipeBookController recipeBookController;

    /**
     * @param inventoryController  the inventory controller
     * @param recipeBookController the recipe book controller
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Controllers are injected on purpose and shared with the rest of the game")
    public CraftingControllerImpl(final InventoryController inventoryController,
            final RecipeBookController recipeBookController) {
        this.inventoryController = inventoryController;
        this.recipeBookController = recipeBookController;
    }

    @Override
    public void craftPotion(final List<Ingredient> ingredients) {
        final Optional<Recipe> found = recipeBookController.findByIngredients(ingredients);
        if (found.isPresent()) {
            final Recipe recipe = found.get();
            final Potion potion = recipe.getPotion();
            inventoryController.addPotion(potion.getName(), potion.getImagePath(), 1,
                    potion.getDescription(), potion.getEffect(), false);
            recipe.setUnlocked();
            for (final Ingredient required : recipe.getIngredients()) {
                inventoryController.removeIngredient(required.getName(), required.getQuantity());
            }
        } else {
            inventoryController.addPotion(BLACK_POTION_NAME, BLACK_POTION_PATH, 1, "A failed attempt...", "none",
                    true);
            for (final Ingredient chosen : ingredients) {
                inventoryController.removeIngredient(chosen.getName(), 1);
            }
        }
    }

    @Override
    public boolean canCraft(final List<Ingredient> ingredients) {
        return recipeBookController.findByIngredients(ingredients).isPresent();
    }

    @Override
    public List<Recipe> getAvailableRecipes() {
        return recipeBookController.getUnlockedRecipes().stream()
                .filter(inventoryController::canCraftPotion)
                .toList();
    }
}
