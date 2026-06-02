package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import java.util.List;
import java.util.ArrayList;

public class InventoryImpl implements Inventory{
    private List<Ingredient> ingredients;
    private List<Potion> potions;

    public InventoryImpl() {
        this.ingredients = new ArrayList<>();
        this.potions = new ArrayList<>();
    }

    @Override public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override public List<Potion> getPotions() {
        return potions;
    }

    @Override public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    @Override public void addPotion(Potion potion) {
        potions.add(potion);
    }

    @Override public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    @Override public void removePotion(Potion potion) {
        potions.remove(potion);
    }
}
