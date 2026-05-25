package it.unibo.KikiStore.model.inventory.api;
import java.util.List;

public interface Inventory {
    public List<Ingredient> getIngredients();
    public List<Potion> getPotions();
    public void addIngredient(Ingredient ingredient);
    public void addPotion(Potion potion);
    public void removeIngredient(Ingredient ingredient);
    public void removePotion(Potion potion);
    public boolean hasIngredient(Ingredient ingredient);
}
