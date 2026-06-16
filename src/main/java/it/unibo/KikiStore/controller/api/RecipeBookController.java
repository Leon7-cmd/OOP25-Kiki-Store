package it.unibo.KikiStore.controller.api;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.List;


public interface RecipeBookController {
    public List<Recipe> getAllRecipes();
    public List<Recipe> getUnlockedRecipes();
    public void unlockRecipe(Recipe recipe);
    public List<Recipe> getCraftableRecipes();
    public List<Recipe> findByEffect(String effect);
    public Recipe findByIngredients(List<Ingredient> ingredients);
    public Recipe findByName(String recipeName);
    public int getUnlockedCount();
}
