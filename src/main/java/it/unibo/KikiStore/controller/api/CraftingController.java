package it.unibo.KikiStore.controller.api;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.List;

public interface CraftingController {
    public void craftPotion(List<Ingredient> ingredients);
    public  boolean canCraft(List<Ingredient> ingredients);
    public List<Recipe> getAvailableRecipes();

}
