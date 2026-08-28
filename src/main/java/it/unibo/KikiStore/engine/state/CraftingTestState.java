package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.CraftingController;
import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.CraftingControllerImpl;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.RecipeBookControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.CraftingState;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Test state that opens CraftingState directly on launch.
 * Adds a few ingredients to the inventory so you can actually
 * select 3 and try brewing (both valid and invalid combinations).
 */
public final class CraftingTestState implements GameState {

        private final InputHandler input;
        private final GameStateManager gsm;
        private boolean initialized;

        /**
         * @param input the input handler
         * @param gsm   the game state manager
         */
        public CraftingTestState(final InputHandler input, final GameStateManager gsm) {
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

                        final RecipeBook recipeBook = new RecipeBookImpl("textFiles/recipes.json");
                        final InventoryController inventoryController = new InventoryControllerImpl();
                        final RecipeBookController recipeBookController = new RecipeBookControllerImpl(recipeBook,
                                        inventoryController);

                        final CraftingController craftingController = new CraftingControllerImpl(inventoryController,
                                        recipeBookController);
                        final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json",
                                        "textFiles/potions.json");
                        final SpriteManager spriteManager = new SpriteManager();

                        // Aggiungi qualche ingrediente di test — prendi 3 che formano
                        // una ricetta valida nel tuo recipes.json così puoi testare
                        // sia il caso "successo" che il caso "fallimento" scegliendo
                        // combinazioni diverse
                        inventoryController.addIngredient(
                                        "Dandelion", "sprites/ingredients/dandelion", 3, "flower",7);
                        inventoryController.addIngredient(
                                        "Chamomile", "sprites/ingredients/chamomile", 3, "flower",3);
                        inventoryController.addIngredient(
                                        "basil", "sprites/ingredients/basil", 3, "plant",2);
                        inventoryController.addIngredient(
                                        "sage", "sprites/ingredients/sage", 3, "plant",6);

                        final GameState craftingState = new CraftingState(
                                        inventoryController,
                                        craftingController,
                                        recipeBookController,
                                        catalog,
                                        spriteManager,
                                        gsm,
                                        this,
                                        input);

                        gsm.setState(craftingState);
                }
        }

        @Override
        public void render(final GraphicsContext gc) {
                // vuoto — sostituito subito da CraftingState
        }
}
