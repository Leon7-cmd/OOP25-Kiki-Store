package it.unibo.KikiStore.controller.impl;
import it.unibo.KikiStore.controller.api.CraftingController;
import it.unibo.KikiStore.controller.api.InventoryController;   
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingControllerImpl implements CraftingController {
    private final InventoryController inventoryController;
    private final RecipeBook recipeBook;

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
           for (Ingredient ingredient : ingredients) {
               inventoryController.removeIngredient(ingredient.getName(), 1); 
           }

        } else  {
            Potion blackPotion = new Potion();//da capire come gestire le pozioni fallite
            blackPotion.setBlack(true);
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
