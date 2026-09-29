package it.unibo.kikistore.engine.state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.controller.impl.InventoryControllerImpl;
import it.unibo.kikistore.controller.impl.PlayerControllerImpl;
import it.unibo.kikistore.controller.impl.RecipeBookControllerImpl;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.inventory.api.GameCatalog;
import it.unibo.kikistore.model.inventory.impl.RecipeBookImpl;
import it.unibo.kikistore.model.player.impl.PlayerImpl;
import it.unibo.kikistore.view.states.menu.api.MenuView;
import it.unibo.kikistore.view.states.menu.impl.MenuViewImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

/**
 * Initial title and main menu screen state.
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "GameStateTransition is needed to manage game startup and state transitions"
)
public final class MenuState implements GameState {

    private static final String RECIPES_CONFIG_PATH = "textFiles/recipes.json";

    private final GameStateTransition transitionController;
    private final InputHandler input;
    private final PlayerController kiki;
    private final InventoryController inventory;
    private final RecipeBookController recipeBookController;
    private final SpriteManager spriteManager;
    private final GameCatalog catalog;
    private final MenuView menuView;

    /**
     * Constructs a new MenuState.
     *
     * @param transitionController the controller handling state transitions
     * @param input                the user input handler
     * @param spriteManager        the manager caching graphic resources
     * @param catalog              the global catalog holding game items and recipes
     */
    public MenuState(
        final GameStateTransition transitionController,
        final InputHandler input,
        final SpriteManager spriteManager,
        final GameCatalog catalog
    ) {
        this.transitionController = transitionController;
        this.input = input;
        this.spriteManager = spriteManager;
        this.catalog = catalog;

        this.inventory = new InventoryControllerImpl();
        this.recipeBookController = new RecipeBookControllerImpl(
            new RecipeBookImpl(RECIPES_CONFIG_PATH),
            this.inventory
        );
        this.kiki = new PlayerControllerImpl(new PlayerImpl(0.0, 0.0), this.input);

        this.menuView = new MenuViewImpl(this.spriteManager);
    }

    @Override
    public void init() { }

    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "System.exit is required to enforce graceful application shutdown, especially on macOS >:("
    )
    @Override
    public void update() {
        if (!input.isMouseClicked()) {
            return;
        }

        final double mouseX = input.getMouseX();
        final double mouseY = input.getMouseY();

        if (menuView.isStartClicked(mouseX, mouseY)) {
            transitionController.pushState(new VillageState(
                transitionController,
                input,
                kiki,
                inventory,
                recipeBookController,
                spriteManager,
                catalog
            ), true);
        } else if (menuView.isQuitClicked(mouseX, mouseY)) {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        menuView.render(gc);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
