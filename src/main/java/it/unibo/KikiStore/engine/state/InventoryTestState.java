package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.InventoryState;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

public class InventoryTestState implements GameState {

    private final InputHandler input;
    private final GameStateManager gsm;
    private boolean initialized = false;

    
    public InventoryTestState(final InputHandler input, final GameStateManager gsm) {
        this.input = input;
        this.gsm = gsm;
    }

    @Override
    public void init() {
        // vuoto, la transizione avviene nel primo update()
    }

    @Override
    public void update() {
        
        if (!initialized) {
            initialized = true;

            final RecipeBook recipeBook = new RecipeBookImpl("recipes.json");

            final InventoryController inventoryController =
                new InventoryControllerImpl(recipeBook);

            // item di test da caricare per testare caricamento corretto degli sprite negli slot
            /*
            inventoryController.addIngredient(
                "Lavender", "assets/ingredients/lavender", 3, "flower");
            inventoryController.addIngredient(
                "Mint", "assets/ingredients/mint", 1, "plant");
            inventoryController.addIngredient(
                "Chamomile", "assets/ingredients/chamomile", 2, "flower");
            inventoryController.addPotion(
                "Sleepy Hollow Potion", "assets/potions/sleepy_hollow",
                1, "Smells of lavender and old books", "sleep", false);
            */
            
            final GameCatalog catalog = new GameCatalogImpl(
                "ingredients.json", "potions.json");

            final SpriteManager spriteManager = new SpriteManager();

            // previousState = this -> premendo ESC torna qui (schermata vuota)
            final GameState inventoryState = new InventoryState(
                inventoryController,
                catalog,
                spriteManager,
                gsm,
                this,
                input
            );

            gsm.setState(inventoryState);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        // questo stato viene subito sostituito da InventoryState
    }
}