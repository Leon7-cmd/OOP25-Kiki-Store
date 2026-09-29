package it.unibo.kikistore.engine.state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.inventory.api.GameCatalog;
import it.unibo.kikistore.model.item.api.ItemSpawner;
import it.unibo.kikistore.model.item.impl.ItemSpawnerImpl;
import it.unibo.kikistore.model.map.api.GameTile;
import it.unibo.kikistore.model.map.impl.CollisionHandler;
import it.unibo.kikistore.model.map.impl.MapLoader;
import it.unibo.kikistore.model.map.impl.TileMapImpl;
import it.unibo.kikistore.model.utility.BoundingBox;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import it.unibo.kikistore.view.states.village.api.VillageView;
import it.unibo.kikistore.view.states.village.impl.VillageViewImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * GameState implementation for the village area.
 * Handles player movement, item spawning and collection, and transitions to minigames or other areas
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "Shared engine/controller components from outside"
)
public final class VillageState implements GameState {
    private static final int TILE_SIZE = 32;
    private static final int[] TELEPORT1 = {2550, 1520};
    private static final int[] TELEPORT2 = {1280, 2410};
    private static final double DEFAULT_SPAWN_X = 1850.0;
    private static final double DEFAULT_SPAWN_Y = 2950.0;

    private static final int TILE_TELEPORT_SHOP_ID = 2;
    private static final int TILE_TELEPORT_HOUSE_ID = 3;
    private static final int TILE_MINIGAME_FLY_ID = 4;
    private static final int TILE_MINIGAME_MEMORY_ID = 5;

    private final InputHandler input;
    private final PlayerController kiki; 
    private final InventoryController inventory;
    private final ItemSpawner itemSpawner;
    private final RecipeBookController recipeBookController;
    private final GameCatalog catalog;

    private final GameStateTransition transitionController;
    private final CollisionHandler collisionHandler;
    private final SpriteManager spriteManager;
    private final VillageView villageView;

    private int frameCount;
    private double savedX;
    private double savedY;

    /**
     * Constructs a new VillageState.
     *
     * @param transitionController the controller for managing game state transitions
     * @param input                the input handler for user interactions
     * @param kiki                 the player controller for Kiki
     * @param inventory            the inventory controller for managing items
     * @param recipeBookController the recipe book controller
     * @param spriteManager        the sprite manager for rendering graphics
     * @param catalog              the game catalog containing item and recipe data
     */
    public VillageState(
        final GameStateTransition transitionController, 
        final InputHandler input, 
        final PlayerController kiki, 
        final InventoryController inventory, 
        final RecipeBookController recipeBookController, 
        final SpriteManager spriteManager, 
        final GameCatalog catalog
    ) {
        this.transitionController = transitionController;
        this.input = input;
        this.kiki = kiki;
        this.recipeBookController = recipeBookController;
        this.inventory = inventory;
        this.catalog = catalog;

        // --- 1. RESOURCE LOADING ---
        final int[][] groundGrid = MapLoader.loadMap("maps/map0/testGround.txt");
        final int[][] decorationGrid = MapLoader.loadMap("maps/map0/testDecor.txt");
        final int[][] upperGrid = MapLoader.loadMap("maps/map0/testUpper.txt");
        final int[][] maskGrid = MapLoader.loadMap("maps/map0/col/testCol.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        this.collisionHandler = new CollisionHandler(collisionMap);

        if (this.kiki.getX() == 0.0 && this.kiki.getY() == 0.0) {
            this.kiki.setPosition(DEFAULT_SPAWN_X, DEFAULT_SPAWN_Y);
        }
        this.itemSpawner = new ItemSpawnerImpl(collisionMap, catalog.getAllIngredients());

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = spriteManager;
        this.villageView = new VillageViewImpl(this.spriteManager, groundGrid, decorationGrid, upperGrid);
    }

    @Override
    public void init() { }

    @Override
    public void pause() {
        this.savedX = this.kiki.getX();
        this.savedY = this.kiki.getY();
    }

    @Override
    public void resume() {
        this.kiki.setPosition(this.savedX, this.savedY);
    }

    @Override
    public void update() {
        kiki.update(collisionHandler);
        handleTileInteractions();
        handleMenuTransitions();

        itemSpawner.checkCollection(kiki, inventory);
        itemSpawner.update();

        frameCount++;
    }

    @Override
    public void render(final GraphicsContext gc) {
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), kiki.maxEnergy(), kiki.getMoney());
        villageView.render(gc, kiki.toRenderData(), itemSpawner.getActiveItems(), hudData, frameCount);
    }

    private void handleTileInteractions() {
        if (!input.isAction()) {
            return;
        }

        final BoundingBox interactionArea = kiki.getHitbox();
        final int tileId = collisionHandler.getInteractableTileId(
            interactionArea.x(),
            interactionArea.y(),
            interactionArea.width(),
            interactionArea.height()
        );

        switch (tileId) {
            case TILE_TELEPORT_SHOP_ID -> this.kiki.setPosition(TELEPORT1[0], TELEPORT1[1]);
            case TILE_TELEPORT_HOUSE_ID -> this.kiki.setPosition(TELEPORT2[0], TELEPORT2[1]);
            case TILE_MINIGAME_FLY_ID -> transitionController.pushState(
                new MinigameFlyState(transitionController, input, kiki, spriteManager), true
            );
            case TILE_MINIGAME_MEMORY_ID -> transitionController.pushState(
                new MemoryState(kiki, catalog, inventory, spriteManager, transitionController, input), true
            );
            default -> {
                // No interaction mapped for this tile
            }
        }
    }

    private void handleMenuTransitions() {
        if (input.isCraftingPressed()) {
            transitionController.pushState(
                new CraftingState(
                    inventory, 
                    recipeBookController, 
                    kiki, 
                    catalog, 
                    spriteManager, 
                    transitionController, 
                    input
                ), true
            );
        } else if (input.isInventoryPressed()) {
            transitionController.pushState(
                new BookState(
                    inventory, 
                    recipeBookController, 
                    catalog, 
                    spriteManager, 
                    transitionController, 
                    input
                ), false
            );
        } else if (input.isEscapePressed()) {
            transitionController.pushState(
                new PauseState(
                    transitionController, 
                    input, 
                    spriteManager, 
                    catalog
                ), false
            );
        }
    }
}
