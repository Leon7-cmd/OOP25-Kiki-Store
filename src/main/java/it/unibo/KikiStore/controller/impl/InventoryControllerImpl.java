package it.unibo.KikiStore.controller.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.KikiStore.model.inventory.impl.PotionImpl;
import it.unibo.KikiStore.model.item.api.GameItem;

public class InventoryControllerImpl implements InventoryController {
    private static final int MAX_CAPACITY = 50;
    private final Inventory inventory = new InventoryImpl();
    private final RecipeBook recipeBook;

    public InventoryControllerImpl(RecipeBook recipeBook) {
        this.recipeBook = recipeBook;
    }
    
    @Override public boolean isFull(){
        return (inventory.getIngredients().size() + inventory.getPotions().size()) == MAX_CAPACITY;
    }

    private GameItem findItem(String name, List<? extends GameItem> list) {//da modificare, magari con un hashmap per ottimizzare la ricerca, e usare filter invece di un ciclo for
        for (GameItem inventoryItem : list) {
            if(inventoryItem.getName().equalsIgnoreCase(name)) {
                return inventoryItem;
            }
        }
        return null;
        //return inventory.getIngredients().contains(ingredient); ----alternative
    }

    @Override public boolean hasIngredient(String name){
        return findItem(name, inventory.getIngredients()) != null;
    }

    @Override public boolean hasPotion(String name){
        return findItem(name, inventory.getPotions()) != null;
    }

    @Override public int getIngredientQuantity(String name) {
        GameItem item = findItem(name, inventory.getIngredients());
        return item != null ? item.getQuantity() : 0;
    }

    @Override public int getPotionQuantity(String name) {
        GameItem item = findItem(name, inventory.getPotions());
        return item != null ? item.getQuantity() : 0;
    }


    @Override public boolean hasEnoughIngredient(String name, int quantity) {//da modificare
        return getIngredientQuantity(name) >= quantity;
    }

    @Override public boolean hasEnoughPotion(String name, int quantity) {//da modificare
        return getPotionQuantity(name) >= quantity;
    }

    @Override public Inventory getInventory(){
        return inventory;
    }

    @Override public void addIngredient(String name, String imagePath, int quantity, String type, int price) {
        if (isFull()) {
            System.out.println("Cannot add " + name + ", inventory is full");
            return;
        }
        GameItem item = findItem(name, inventory.getIngredients());
        
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            return;
        }
        inventory.addIngredient(new IngredientImpl(name, imagePath, quantity, type, price));
        return;
    }

    @Override public void addPotion(String name, String imagePath, int quantity, String description, String effect, boolean isBlack) {
        if (isFull()) {
            System.out.println("Cannot add " + name + ", inventory is full");
            return;
        }
        GameItem item = findItem(name, inventory.getPotions());
        
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            return;
        }
        inventory.addPotion(new PotionImpl(name, imagePath, quantity, description, effect, isBlack));
        return;
    }


    @Override public void removeIngredient(String name, int quantity) {
        if(hasEnoughIngredient(name, quantity)) {
            GameItem item = findItem(name, inventory.getIngredients());
        
            if (item != null) {
                item.setQuantity(item.getQuantity() - quantity);
                return;
            }
        }
        return;
    }

    @Override public void removePotion(String name, int quantity) {
        if(hasEnoughPotion(name, quantity)) {
            GameItem item = findItem(name, inventory.getPotions());
        
            if (item != null) {
                item.setQuantity(item.getQuantity() - quantity);
                return;
            }
        }
        return;
    }

    @Override public boolean canCraftPotion(Recipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughIngredient(ingredient.getName(), ingredient.getQuantity())) {
                return false;
            }
        }
        return true;
        // or otherwise to implement DRY concept -> return getMissingIngredients(recipe).isEmpty();
    }

    @Override public List<Ingredient> getMissingIngredients(Recipe recipe) {
        List<Ingredient> missing = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughIngredient(ingredient.getName(), ingredient.getQuantity())) {
                missing.add(ingredient);
            }
        }
        return missing;
    }


}
