package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.OrderControllerImpl;
import it.unibo.KikiStore.controller.impl.RecipeBookControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.BookState;
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
import it.unibo.KikiStore.model.player.api.Player;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Test state that opens BookState directly on launch.
 * Added some test items to see colored vs gray slots
 * and one unlocked recipe.
 */
public class BookTestState implements GameState {

    private final InputHandler input;
    private final GameStateManager gsm;
    private boolean initialized;

    /**
     * @param input the input handler
     * @param gsm the game state manager
     */
    public BookTestState(final InputHandler input, final GameStateManager gsm) {
        this.input = input;
        this.gsm = gsm;
    }

    @Override
    public void init() {
        // vuoto — la transizione avviene al primo update()
    }

    @Override
    public void update() {
        if (!initialized) {
            initialized = true;
            final Player player = new PlayerImpl(35,64);
            final OrderBook orderBook = new OrderBookImpl();
            final RecipeBook recipeBook = new RecipeBookImpl("textFiles/recipes.json");
            final InventoryController inventoryController = new InventoryControllerImpl(recipeBook);
            final RecipeBookController recipeBookController =
                new RecipeBookControllerImpl(recipeBook, inventoryController);
            final PotionPriceCalculator priceCalculator=new PotionPriceCalculatorImpl(5);
            final Inventory inventory = new InventoryImpl();
            final OrderController orderController = new OrderControllerImpl(orderBook,recipeBook,inventory,player,priceCalculator);
            final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
            final SpriteManager spriteManager = new SpriteManager();

            // Item di test — appariranno colorati, il resto grigio
            inventoryController.addIngredient(
                "Chamomile", "sprites/ingredients/chamomile", 3, "flower", 2);
            inventoryController.addIngredient(
                "Clover", "sprites/ingredients/clover", 2, "plant", 1);
            inventoryController.addPotion(
                "Shieldberry Potion", "sprites/potions/shieldberry",
                1, "Smells of lavender and old books, perfect for restless nights", "sleep", false);

            // Dopo aver creato recipeBookController, sblocca ricette per test
            List<Recipe> allRecipes = recipeBookController.getAllRecipes();
            if (!allRecipes.isEmpty()) {
                recipeBookController.unlockRecipe(allRecipes.get(0));
                recipeBookController.unlockRecipe(allRecipes.get(1));
                recipeBookController.unlockRecipe(allRecipes.get(2));
                recipeBookController.unlockRecipe(allRecipes.get(3));
            }
            final GameState bookState = new BookState(
                inventoryController, recipeBookController,orderController, catalog,
                spriteManager, gsm, this, input
            );

            gsm.setState(bookState);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        // vuoto — sostituito da BookState
    }
}