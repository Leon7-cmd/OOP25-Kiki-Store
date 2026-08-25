package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.MemoryController;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.model.player.api.Player;
import it.unibo.KikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.KikiStore.controller.impl.MemoryControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.MemoryState;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Test state that opens MemoryState directly on launch, using a
 * placeholder PlayerStats that just logs the rewards to the console.
 */
public class MemoryTestState implements GameState {

    private final InputHandler input;
    private final GameStateManager gsm;
    private boolean initialized;

    /**
     * @param input the input handler
     * @param gsm   the game state manager
     */
    public MemoryTestState(final InputHandler input, final GameStateManager gsm) {
        this.input = input;
        this.gsm = gsm;
    }

    @Override
    public void init() {
        // vuoto - la transizione avviene al primo update()
    }

    @Override
    public void update() {
        if (!initialized) {
            initialized = true;

            final InventoryController inventoryController = new InventoryControllerImpl();
            final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
            final SpriteManager spriteManager = new SpriteManager();
            final Player player = new PlayerImpl(0, 0);

            final MemoryController memoryController = new MemoryControllerImpl(catalog, inventoryController,
                    player);

            final GameState memoryState = new MemoryState(
                    memoryController,
                    spriteManager,
                    gsm,
                    this,
                    input);

            gsm.setState(memoryState);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        // vuoto - sostituito subito da MemoryState
    }
}
