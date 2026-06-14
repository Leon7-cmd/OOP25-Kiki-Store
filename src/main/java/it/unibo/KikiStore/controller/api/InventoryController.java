package it.unibo.KikiStore.controller.api;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.List;

public interface InventoryController {

    public void addIngredient(String name, String imagePath, int quantity, String type);
    public void addPotion(String name, String imagePath, int quantity, String description, String effect, boolean isBlack);

    public void removeIngredient(String name, int quantity);
    public void removePotion(String name, int quantity); 

    public boolean hasIngredient(String name);
    public boolean hasPotion(String name);

    public boolean hasEnoughIngredient(String name, int quantity);
    public boolean hasEnoughPotion(String name, int quantity);

    public List<Ingredient> getMissingIngredients(Recipe recipe);
    public boolean canCraftPotion(Recipe recipe);
    public Inventory getInventory();

    public int getIngredientQuantity(String name);
    public int getPotionQuantity(String name);
    
    public boolean isFull();
}
