package it.unibo.KikiStore.engine.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.RecipeBookControllerImpl;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;

class ShopStateDefaultsTest {

    @Test
    void seedStarterInventoryAddsRealContent() {
        final var catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
        final var inventoryController = new InventoryControllerImpl();

        GameSession.seedStarterInventory(inventoryController, catalog);

        assertTrue(inventoryController.getIngredientQuantity("Chamomile") > 0
                || inventoryController.getIngredientQuantity("Clover") > 0
                || inventoryController.getPotionQuantity("Shieldberry Potion") > 0);
    }

    @Test
    void unlockStarterRecipesUnlocksSomeRecipes() {
        final var recipeBook = new RecipeBookImpl("textFiles/recipes.json");
        final var inventoryController = new InventoryControllerImpl();
        final var recipeBookController = new RecipeBookControllerImpl(recipeBook, inventoryController);

        GameSession.unlockStarterRecipes(recipeBookController);

        assertFalse(recipeBook.getUnlockedRecipes().isEmpty());
    }
}
