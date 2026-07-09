package it.unibo.KikiStore.model.inventory.api;
import java.util.List;
public interface RecipeBook {
    public List<Recipe> getRecipes();
    public List<Recipe> getUnlockedRecipes();
    public List<Recipe> findByEffect(String effect);
    public Recipe findByIngredients(List<Ingredient> ingredients);
    
}
