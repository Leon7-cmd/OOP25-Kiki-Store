package it.unibo.KikiStore.model.inventory.api;
import java.util.List;


public interface Recipe {
    public List<Ingredient> getIngredients();
    public Potion getPotion();
    public boolean isUnlocked();
    public void setUnlocked();
}
