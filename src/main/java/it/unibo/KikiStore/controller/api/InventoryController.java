package it.unibo.KikiStore.controller.api;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.List;

public interface InventoryController {
    public void addIngredient(String id, double x, double y, double width, double height, boolean animated, String name, int quantity, String type);
    public void addPotion(String id, double x, double y, double width, double height, boolean animated, String name, int quantity, String description, String effect, boolean isBlack);
    public void removeIngredient(String id, int quantity);
    public void removePotion(String id, int quantity); 
    public boolean hasIngredient(String id);
    public boolean hasEnoughIngredient(String id, int quantity);
    public boolean canCraftPotion(Recipe recipe);
    public List<Ingredient> getMissingIngnredients(Recipe recipe);
    public Inventory getInventory();
    public int getIngredientQuantity(String id);
    public boolean isFull();
    public boolean isEmpty();
}
