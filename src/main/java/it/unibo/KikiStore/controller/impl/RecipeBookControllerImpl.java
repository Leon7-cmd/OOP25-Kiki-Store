package it.unibo.KikiStore.controller.impl;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeBookControllerImpl implements RecipeBookController{
    private final RecipeBook recipeBook;
    private final InventoryController inventoryController;

    public RecipeBookControllerImpl(RecipeBook recipeBook, InventoryController inventoryController) {
        this.recipeBook = recipeBook;
        this.inventoryController = inventoryController;
    }

    @Override public List<Recipe> getAllRecipes(){
        return recipeBook.getRecipes();
    }

    @Override public List<Recipe> getUnlockedRecipes(){
        return recipeBook.getUnlockedRecipes();
    }

    @Override public List<Recipe> findByEffect(String effect){
        return recipeBook.findByEffect(effect);
    }

    @Override public void unlockRecipe(Recipe recipe){
        recipe.setUnlocked();
    }

    @Override public List<Recipe> getCraftableRecipes(){//sistema getavailableRecipes in creftingController
        List<Recipe> craftableRecipes = new ArrayList<>();

        for (Recipe recipe : recipeBook.getUnlockedRecipes()) {
            if (inventoryController.canCraftPotion(recipe)) {
                craftableRecipes.add(recipe);
            }
        }

        return craftableRecipes;
    }
}
