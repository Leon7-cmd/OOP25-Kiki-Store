package it.unibo.KikiStore.controller.impl;
import it.unibo.KikiStore.controller.api.CraftingController;
import it.unibo.KikiStore.controller.api.InventoryController;   
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingControllerImpl implements CraftingController {
    private final InventoryController inventoryController;
    private final RecipeBook recipeBook;
    private static final String BLACK_POTION_NAME = "Failed Potion";
    private static final String BLACK_POTION_PATH = "assets/potions/black.png";

    public CraftingControllerImpl(InventoryController inventoryController, RecipeBook recipeBook) {
        this.inventoryController = inventoryController;
        this.recipeBook = recipeBook;
    }

    @Override public void craftPotion(List<Ingredient> ingredients) {
        Recipe recipe = recipeBook.findByIngredients(ingredients);
        if(recipe != null) {
           Potion potion = recipe.getPotion();
           inventoryController.addPotion(potion.getName(), potion.getImagePath(), potion.getQuantity(), potion.getDescription(), potion.getEffect(), false);
           recipe.setUnlocked();
           //inventoryController.removeIngredients(ingredients);//da sistemare, serve metodo che prende lista
           for (Ingredient ingredient : recipe.getIngredients()) {
                inventoryController.removeIngredient(ingredient.getName(), ingredient.getQuantity());
            }

        } else  {
            inventoryController.addPotion(BLACK_POTION_NAME, BLACK_POTION_PATH, 1, "A failed attempt...", "none", true);
            //blackPotion.setBlack(true);metodo probabilmente da togliere da potion
        }
    }

    @Override public boolean canCraft(List<Ingredient> ingredients) {
        Recipe recipe = recipeBook.findByIngredients(ingredients);
        
        return recipe != null;
    }

    @Override public List<Recipe> getAvailableRecipes() {
        List<Recipe> availableRecipes = new ArrayList<>();
        List<Recipe> allRecipes = recipeBook.getUnlockedRecipes();

        for (Recipe recipe : allRecipes) {
            if (inventoryController.canCraftPotion(recipe)) {
                availableRecipes.add(recipe);
            }
        }

        return availableRecipes;
    }

}
