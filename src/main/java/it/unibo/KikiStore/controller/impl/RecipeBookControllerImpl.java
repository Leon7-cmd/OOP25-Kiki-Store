package it.unibo.KikiStore.controller.impl;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
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

    @Override public Recipe findByIngredients(List<Ingredient> ingredients) {
        for (Recipe recipe : recipeBook.getRecipes()) {
            if (matchesIngredients(recipe.getIngredients(), ingredients)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matchesIngredients(List<Ingredient> recipeIngredients, List<Ingredient> selected) {
        if (recipeIngredients.size() != selected.size()) {
            return false;
        }
        for (Ingredient required : recipeIngredients) {
            boolean found = false;
            for (Ingredient chosen : selected) {
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

    @Override public List<Recipe> findByEffect(String effect) {
        List<Recipe> matchingRecipes = new ArrayList<>();
        for (Recipe recipe : recipeBook.getRecipes()) {
            if (recipe.getPotion().getEffect().toLowerCase().contains(effect.toLowerCase())) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
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

    @Override public Recipe findByName(String recipeName){
        for (Recipe recipe : recipeBook.getRecipes()) {
            if (recipe.getPotion().getName().toLowerCase().contains(recipeName.toLowerCase())) {
                return recipe;
            }
        }

        return null;
    }

    @Override public int getUnlockedCount(){
        return recipeBook.getUnlockedRecipes().size();
    }
}
