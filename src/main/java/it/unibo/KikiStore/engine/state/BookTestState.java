package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.RecipeBookControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.BookState;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
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

            final RecipeBook recipeBook = new RecipeBookImpl("recipes.json");
            final InventoryController inventoryController = new InventoryControllerImpl(recipeBook);
            final RecipeBookController recipeBookController =
                new RecipeBookControllerImpl(recipeBook, inventoryController);
            final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
            final SpriteManager spriteManager = new SpriteManager();

            // Item di test — appariranno colorati, il resto grigio
            inventoryController.addIngredient(
                "Chamomile", "sprites/ingredients/chamomile", 3, "flower");
            inventoryController.addIngredient(
                "Clover", "sprites/ingredients/clover", 2, "plant");
            inventoryController.addPotion(
                "Shieldberry Potion", "sprites/potions/shieldberry",
                1, "Smells of lavender and old books, perfect for restless nights", "sleep", false);

            final GameState bookState = new BookState(
                inventoryController, recipeBookController, catalog,
                spriteManager, gsm, this, input
            );

            gsm.setState(bookState);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        // vuoto — sostituito subito da BookState
    }
}