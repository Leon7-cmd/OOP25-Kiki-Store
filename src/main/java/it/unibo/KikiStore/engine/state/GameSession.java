package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.OrderControllerImpl;
import it.unibo.KikiStore.controller.impl.RecipeBookControllerImpl;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.economy.impl.PotionPriceCalculatorImpl;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.order.impl.OrderBookImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;

/**
 * Shared gameplay session used across map, shop, book, crafting and orders.
 * This avoids creating separate empty inventories and recipe books each time a UI is opened.
 */
public final class GameSession {
    private final PlayerImpl player;
    private final GameCatalog catalog;
    private final Inventory inventory;
    private final InventoryController inventoryController;
    private final RecipeBook recipeBook;
    private final RecipeBookController recipeBookController;
    private final OrderBook orderBook;
    private final OrderController orderController;

    public static GameSession createStarterSession() {
        final PlayerImpl player = new PlayerImpl(870, 920);
        final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
        final Inventory inventory = new InventoryImpl();
        final InventoryController inventoryController = new InventoryControllerImpl();
        final RecipeBook recipeBook = new RecipeBookImpl("textFiles/recipes.json");
        final RecipeBookController recipeBookController = new RecipeBookControllerImpl(recipeBook, inventoryController);
        final OrderBook orderBook = new OrderBookImpl();
        final PotionPriceCalculator priceCalculator = new PotionPriceCalculatorImpl(5);
        final OrderController orderController = new OrderControllerImpl(orderBook, recipeBook, inventory, player, priceCalculator);

        seedStarterInventory(inventoryController, catalog);
        unlockStarterRecipes(recipeBookController);

        return new GameSession(player, catalog, inventory, inventoryController, recipeBook, recipeBookController, orderBook, orderController);
    }
//created now here but is it really state or state controller? how about passing parameters?

    public static void seedStarterInventory(final InventoryController inventoryController, final GameCatalog catalog) {
        inventoryController.addIngredient("Chamomile", "sprites/ingredients/chamomile", 3, "flower", 3);
        inventoryController.addIngredient("Clover", "sprites/ingredients/clover", 2, "plant", 12);
        inventoryController.addIngredient("Blue Berries", "sprites/ingredients/blue_berries", 4, "berry", 7);
        inventoryController.addIngredient("Basil", "sprites/ingredients/basil", 2, "plant", 2);
        inventoryController.addPotion("Shieldberry Potion", "sprites/potions/shieldberry", 1,
                "Smells of dark berries and white blossoms, keeps colds and coughs at bay",
                "immunity", false);
    }

    public static void unlockStarterRecipes(final RecipeBookController recipeBookController) {
        final List<Recipe> allRecipes = recipeBookController.getAllRecipes();
        for (int i = 0; i < Math.min(3, allRecipes.size()); i++) {
            recipeBookController.unlockRecipe(allRecipes.get(i));
        }
    }

    private GameSession(final PlayerImpl player,
            final GameCatalog catalog,
            final Inventory inventory,
            final InventoryController inventoryController,
            final RecipeBook recipeBook,
            final RecipeBookController recipeBookController,
            final OrderBook orderBook,
            final OrderController orderController) {
        this.player = player;
        this.catalog = catalog;
        this.inventory = inventory;
        this.inventoryController = inventoryController;
        this.recipeBook = recipeBook;
        this.recipeBookController = recipeBookController;
        this.orderBook = orderBook;
        this.orderController = orderController;
    }

    public PlayerImpl getPlayer() {
        return player;
    }

    public GameCatalog getCatalog() {
        return catalog;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public RecipeBook getRecipeBook() {
        return recipeBook;
    }

    public RecipeBookController getRecipeBookController() {
        return recipeBookController;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public OrderController getOrderController() {
        return orderController;
    }
}
