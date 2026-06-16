package it.unibo.KikiStore.controller.api;
import it.unibo.KikiStore.model.inventory.api.Recipe;

import java.util.List;


public interface RecipeBookController {
    public List<Recipe> getAllRecipes();
    public List<Recipe> getUnlockedRecipes();
    public List<Recipe> findByEffect(String effect);
    public void unlockRecipe(Recipe recipe);
    public List<Recipe> getCraftableRecipes();
}
