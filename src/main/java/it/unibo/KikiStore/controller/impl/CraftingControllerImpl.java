package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.CraftingController;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the potion crafting logic - matching selected ingredients
 * against known recipes and updating the inventory accordingly.
 */
public final class CraftingControllerImpl implements CraftingController {
    private static final String BLACK_POTION_NAME = "Failed Potion";
    private static final String BLACK_POTION_PATH = "assets/potions/black.png";

    private final InventoryController inventoryController;
    private final RecipeBookController recipeBookController;

    /**
     * @param inventoryController the inventory controller
     * @param recipeBookController the recipe book controller
     */
    public CraftingControllerImpl(final InventoryController inventoryController,
            final RecipeBookController recipeBookController) {
        this.inventoryController = inventoryController;
        this.recipeBookController = recipeBookController;
    }

    @Override
    public void craftPotion(final List<Ingredient> ingredients) {
        final Recipe recipe = recipeBookController.findByIngredients(ingredients);
        if (recipe != null) {
            final Potion potion = recipe.getPotion();
            inventoryController.addPotion(potion.getName(), potion.getImagePath(), potion.getQuantity(),
                    potion.getDescription(), potion.getEffect(), false);
            recipe.setUnlocked();
            // TO-DO: inventoryController.removeIngredients(ingredients) da sistemare, serve un metodo che prende una lista di ingredienti
            for (final Ingredient ingredient : recipe.getIngredients()) {
                inventoryController.removeIngredient(ingredient.getName(), ingredient.getQuantity());
            }

        } else {
            inventoryController.addPotion(BLACK_POTION_NAME, BLACK_POTION_PATH, 1, "A failed attempt...", "none",
            true);
            // blackPotion.setBlack(true);metodo probabilmente da togliere da potion
        }
    }

    @Override
    public boolean canCraft(final List<Ingredient> ingredients) {
        final Recipe recipe = recipeBookController.findByIngredients(ingredients);

        return recipe != null;
    }

    @Override
    public List<Recipe> getAvailableRecipes() {
        final List<Recipe> availableRecipes = new ArrayList<>();
        final List<Recipe> allRecipes = recipeBookController.getUnlockedRecipes();

        for (final Recipe recipe : allRecipes) {
            if (inventoryController.canCraftPotion(recipe)) {
                availableRecipes.add(recipe);
            }
        }

        return availableRecipes;
    }

}
