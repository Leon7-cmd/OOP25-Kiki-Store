package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.PotionImpl;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;

public class InventoryControllerImpl implements InventoryController {
    private static final int MAX_CAPACITY = 50;
    private final Inventory inventory = new InventoryImpl();
    private final RecipeBook recipeBook;

    public InventoryControllerImpl(RecipeBook recipeBook) {
        this.recipeBook = recipeBook;
    }
    
    @Override public boolean isFull(){
        if((inventory.getIngredients().size() + inventory.getPotions().size()) == MAX_CAPACITY) {
            return true;
        }else 
            return false;
    }

    @Override public boolean hasIngredient(Ingredient ingredient){
        for (Ingredient ing : inventory.getIngredients()) {
            if(ing.getName() == ingredient.getName()) {
                return true;
            }
        }
        return false;

        //return inventory.getIngredients().contains(ingredient); ----alternative
    }

    @Override public boolean hasEnoughIngredient(String name, int quantity) {//da modificare
        
        return getIngredientQuantity(name) >= quantity;
    }
}
