package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.OrderSpawner;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.OrderControllerImpl;
import it.unibo.KikiStore.controller.impl.OrderSpawnerImpl;
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
import it.unibo.KikiStore.model.order.api.CustomerBook;
import it.unibo.KikiStore.model.order.api.NeedBook;
import it.unibo.KikiStore.model.order.api.NeedGenerator;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.order.api.OrderGenerator;
import it.unibo.KikiStore.model.order.impl.CustomerBookImpl;
import it.unibo.KikiStore.model.order.impl.NeedBookImpl;
import it.unibo.KikiStore.model.order.impl.NeedGeneratorImpl;
import it.unibo.KikiStore.model.order.impl.OrderBookImpl;
import it.unibo.KikiStore.model.order.impl.OrderGeneratorImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;

/**
 * Shared gameplay session used across map, shop, book, crafting and orders.
 * This avoids creating separate empty inventories, recipe books, and order books
 * each time a UI is opened — everything is created exactly once here.
 */
public final class GameSession {

    private static final int SPAWN_INTERVAL_FRAMES = 300;
    private static final int MAX_PENDING_ORDERS = 5;
    private static final int SPAWN_RESET_THRESHOLD = 1000;

    private final PlayerImpl player;
    private final GameCatalog catalog;
    private final Inventory inventory;
    private final InventoryController inventoryController;
    private final RecipeBook recipeBook;
    private final RecipeBookController recipeBookController;
    private final OrderBook orderBook;
    private final OrderController orderController;
    private final NeedBook needBook;
    private final CustomerBook customerBook;
    private final NeedGenerator needGenerator;
    private final OrderGenerator orderGenerator;
    private final OrderSpawner orderSpawner;

    public static GameSession createStarterSession() {
        final PlayerImpl player = new PlayerImpl(870, 920);
        final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
        final Inventory inventory = new InventoryImpl();
        final InventoryController inventoryController = new InventoryControllerImpl(inventory);
        final RecipeBook recipeBook = new RecipeBookImpl("textFiles/recipes.json");
        final RecipeBookController recipeBookController = new RecipeBookControllerImpl(recipeBook, inventoryController);
        final OrderBook orderBook = new OrderBookImpl();
        final PotionPriceCalculator priceCalculator = new PotionPriceCalculatorImpl(5);
        final OrderController orderController = new OrderControllerImpl(orderBook, recipeBook, inventory, player, priceCalculator);

        final NeedBook needBook = new NeedBookImpl("textFiles/needs.json");
        final CustomerBook customerBook = new CustomerBookImpl("textFiles/customers.json", catalog);
        final NeedGenerator needGenerator = new NeedGeneratorImpl(needBook);
        final OrderGenerator orderGenerator = new OrderGeneratorImpl(customerBook, needGenerator);
        final OrderSpawner orderSpawner = new OrderSpawnerImpl(
            orderGenerator, orderBook, SPAWN_INTERVAL_FRAMES, MAX_PENDING_ORDERS, SPAWN_RESET_THRESHOLD
        );

        seedStarterInventory(inventoryController, catalog);
        unlockStarterRecipes(recipeBookController);

        return new GameSession(player, catalog, inventory, inventoryController, recipeBook, recipeBookController,
                orderBook, orderController, needBook, customerBook, needGenerator, orderGenerator, orderSpawner);
    }

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
            final OrderController orderController,
            final NeedBook needBook,
            final CustomerBook customerBook,
            final NeedGenerator needGenerator,
            final OrderGenerator orderGenerator,
            final OrderSpawner orderSpawner) {
        this.player = player;
        this.catalog = catalog;
        this.inventory = inventory;
        this.inventoryController = inventoryController;
        this.recipeBook = recipeBook;
        this.recipeBookController = recipeBookController;
        this.orderBook = orderBook;
        this.orderController = orderController;
        this.needBook = needBook;
        this.customerBook = customerBook;
        this.needGenerator = needGenerator;
        this.orderGenerator = orderGenerator;
        this.orderSpawner = orderSpawner;
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

    public NeedBook getNeedBook() {
        return needBook;
    }

    public CustomerBook getCustomerBook() {
        return customerBook;
    }

    public NeedGenerator getNeedGenerator() {
        return needGenerator;
    }

    public OrderGenerator getOrderGenerator() {
        return orderGenerator;
    }

    public OrderSpawner getOrderSpawner() {
        return orderSpawner;
    }
}