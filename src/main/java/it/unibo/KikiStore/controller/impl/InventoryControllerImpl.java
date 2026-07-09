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
            if(ing.getName().equals(ingredient.getName())) {
                return true;
            }
        }
        return false;

        //return inventory.getIngredients().contains(ingredient); ----alternative
    }

    @Override public boolean hasEnoughIngredient(String name, int quantity) {//da modificare
        
        return getIngredientQuantity(name) >= quantity;
    }
     // NUOVO METODO AGGIUNTO PER RISOLVERE L'ERRORE
    @Override public int getIngredientQuantity(String name) {
        for (Ingredient ing : inventory.getIngredients()) {
            if (ing.getName().equals(name)) {
                // Presumo che l'oggetto Ingredient abbia un metodo getQuantity(). 
                // Se si chiama in un altro modo (es. getAmount()), cambialo qui sotto.
                return ing.getQuantity(); 
            }
        }
        return 0; // Ritorna 0 se l'ingrediente non viene trovato nell'inventario
    }
     // METODO AGGIUNTO ORA PER RESTITUIRE L'INVENTARIO
    @Override public Inventory getInventory() {
        return this.inventory;
    }
    // CORRETTO: Usa getIngredients() della ricetta
    @Override
    public boolean canCraftPotion(Recipe recipe) {
        for (Ingredient reqIngredient : recipe.getIngredients()) {
            if (!hasEnoughIngredient(reqIngredient.getName(), reqIngredient.getQuantity())) {
                return false; 
            }
        }
        return true; 
    }

     // CORRETTO: Adesso restituisce una List<Ingredient> come richiesto
    @Override
    public List<Ingredient> getMissingIngredients(Recipe recipe) {
        List<Ingredient> missing = new ArrayList<>();
        for (Ingredient reqIngredient : recipe.getIngredients()) {
            int currentQty = getIngredientQuantity(reqIngredient.getName());
            if (currentQty < reqIngredient.getQuantity()) {
                int missingQty = reqIngredient.getQuantity() - currentQty;
                
                // Creo un nuovo ingrediente con la quantità mancante.
                // Se IngredientImpl ha un costruttore diverso, adattalo qui.
                missing.add(new IngredientImpl(reqIngredient.getName(),null, missingQty, 0, null)); 
            }
        }
        return missing;

    }

      


}
