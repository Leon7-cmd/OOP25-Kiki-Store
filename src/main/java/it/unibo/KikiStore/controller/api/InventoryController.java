package it.unibo.KikiStore.controller.api;

import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;

public interface InventoryController {
    
    public boolean hasIngredient(Ingredient ingredient);
    public boolean hasEnoughIngredient(String name, int quantity);
    public List<Ingredient> getMissingIngredients(Recipe recipe);
    public boolean canCraftPotion(Recipe recipe);
    public Inventory getInventory();
    public int getIngredientQuantity(String name);
    public boolean isFull();
}
