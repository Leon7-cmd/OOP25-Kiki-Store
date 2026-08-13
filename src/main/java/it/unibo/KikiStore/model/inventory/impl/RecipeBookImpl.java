package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RecipeBookImpl implements RecipeBook {
    private List<Recipe> allRecipes;
    public RecipeBookImpl(String jsonFile){
        allRecipes = new ArrayList<>();
        loadFromJson(jsonFile);
    }

    private void loadFromJson(String jsonFile) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("textFiles/recipes.json");
        if (stream == null) {
            return;
        }
        InputStreamReader reader = new InputStreamReader(stream);
        JsonArray recipes = new Gson().fromJson(reader, JsonArray.class);
        
        for (JsonElement entry : recipes) {
            JsonObject recipeData = entry.getAsJsonObject();
            String name = recipeData.get("name").getAsString();
            String description = recipeData.get("description").getAsString();
            String effect = recipeData.get("effect").getAsString();
            String id = recipeData.get("id").getAsString();
            JsonArray ingredientsArray = recipeData.get("ingredients").getAsJsonArray();
            List<Ingredient> ingredients = new ArrayList<>();

            for (JsonElement ing : ingredientsArray) {
                String ingName = ing.getAsString();
                ingredients.add(new IngredientImpl(ingName, id, 0, "plant"));
            }
            Potion potion = new PotionImpl(name, id, 0, description, effect, false);
            Recipe recipe = new RecipeImpl(ingredients, potion, false);
            allRecipes.add(recipe);
        }
    }

    @Override public List<Recipe> getRecipes() {
        return allRecipes;
    }

    @Override public List<Recipe> getUnlockedRecipes() {
        List<Recipe> unlockedRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.isUnlocked()) {
                unlockedRecipes.add(recipe);
            }
        }
        return unlockedRecipes;
    }

    

    
}
