package it.unibo.kikiStore.engine.state;

import it.unibo.kikiStore.controller.api.InputHandler;
import it.unibo.kikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.kikiStore.engine.api.GameState;
import it.unibo.kikiStore.engine.api.GameStateTransition;
import it.unibo.kikiStore.model.inventory.api.GameCatalog;
import it.unibo.kikiStore.model.item.api.GroundItem;
import it.unibo.kikiStore.model.item.api.ItemSpawner;
import it.unibo.kikiStore.model.item.impl.ItemSpawnerImpl;
import it.unibo.kikiStore.model.map.api.GameTile;
import it.unibo.kikiStore.model.map.impl.CollisionHandler;
import it.unibo.kikiStore.model.map.impl.MapLoader;
import it.unibo.kikiStore.model.map.impl.TileMapImpl;
import it.unibo.kikiStore.view.entity.api.EntityRenderData;
import it.unibo.kikiStore.view.entity.impl.EntityRenderer;
import it.unibo.kikiStore.view.environment.api.MapRenderData;
import it.unibo.kikiStore.view.environment.impl.MapRenderer;
import it.unibo.kikiStore.view.hud.api.HUDRenderData;
import it.unibo.kikiStore.view.hud.impl.HUDRenderer;
import it.unibo.kikiStore.view.item.api.ItemRenderData;
import it.unibo.kikiStore.view.item.impl.ItemRenderer;
import it.unibo.kikiStore.view.utility.Camera;
import it.unibo.kikiStore.view.utility.SpriteManager;
import it.unibo.kikiStore.controller.api.InventoryController;
import it.unibo.kikiStore.controller.api.PlayerController;
import it.unibo.kikiStore.controller.api.RecipeBookController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * GameState implementation for the village area.
 * Handles player movement, item spawning and collection, and transitions to minigames or other areas
 */
public final class VillageState implements GameState {
    private static final int TILE_SIZE = 32;
    private static final int ITEM_SPAWN_COUNT = 10;
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
    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    private final ItemRenderer itemRenderer;
    private final HUDRenderer hudRenderer;

    private final Camera cam = new Camera();
    private int frameCount;
    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] upperGrid;
    private final int[][] maskGrid;
    private double savedX;
    private double savedY;

    /**
     * Constructs a new VillageState.
     *
     * @param transitionController the controller for managing game state transitions
     * @param input                the input handler for user interactions
     * @param kiki                 the player controller for Kiki
     * @param inventory            the inventory controller for managing items
     * @param recipeBookController the recipe book controller for crafting
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

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/map0/testGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/map0/testDecor.txt");
        this.upperGrid = MapLoader.loadMap("maps/map0/testUpper.txt");
        this.maskGrid = MapLoader.loadMap("maps/map0/col/testCol.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        this.collisionHandler = new CollisionHandler(collisionMap);

        if (this.kiki.getX() == 0.0 && this.kiki.getY() == 0.0) {
            this.kiki.setPosition(DEFAULT_SPAWN_X, DEFAULT_SPAWN_Y);
        } 

        this.inventory = new InventoryControllerImpl();
        this.catalog = catalog;

        this.itemSpawner = new ItemSpawnerImpl(collisionMap, catalog.getAllIngredients());
        this.itemSpawner.spawnRandomItems(ITEM_SPAWN_COUNT);

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = spriteManager;
        this.entityRenderer = new EntityRenderer(this.spriteManager);
        this.environmentRenderer = new MapRenderer(this.spriteManager);
        this.itemRenderer = new ItemRenderer(this.spriteManager);
        this.hudRenderer = new HUDRenderer(this.spriteManager);
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
        this.kiki.setCollisionHandler(this.collisionHandler);
    }

    @Override
    public void update() {
        kiki.update();

        itemSpawner.checkCollection(kiki, inventory);
        itemSpawner.update();

        frameCount++;

        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);
        if (tileId == TILE_TELEPORT_SHOP_ID && input.isAction()) {
            this.kiki.setPosition(TELEPORT1[0], TELEPORT1[1]);
        }
        if (tileId == TILE_TELEPORT_HOUSE_ID && input.isAction()) {
            this.kiki.setPosition(TELEPORT2[0], TELEPORT2[1]);
        }
        if (tileId == TILE_MINIGAME_FLY_ID && input.isAction()) {
            transitionController.pushState(
                new MinigameFlyState(
                    transitionController, 
                    input, 
                    kiki
                )
            );
        }
        if (tileId == TILE_MINIGAME_MEMORY_ID && input.isAction()) {
            transitionController.pushState(
                new MemoryState(
                    kiki, 
                    catalog, 
                    inventory, 
                    spriteManager, 
                    transitionController, 
                    input
                )
            );
        }

        if (input.isCraftingPressed()) {
            transitionController.pushState(
                new CraftingState(
                    inventory, 
                    recipeBookController, 
                    catalog, 
                    spriteManager, 
                    transitionController, 
                    input
                )
            );
        }
        if (input.isInventoryPressed()) {
            transitionController.pushState(
                new BookState(
                    inventory, 
                    recipeBookController, 
                    catalog, 
                    spriteManager, 
                    transitionController, 
                    input
                )
            );
        }
        if (input.isEscapePressed()) {
            transitionController.pushState(
                new PauseState(
                    transitionController, 
                    input, 
                    spriteManager, 
                    catalog
                )
            );
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();

        gc.save();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        cam.update(kiki.getX(), kiki.getY(), screenWidth, screenHeight);
        gc.setImageSmoothing(false);
        gc.scale(cam.getZoom(), cam.getZoom());
        gc.translate(-cam.getX(), -cam.getY());

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));

        // Ground Items Layer
        final List<ItemRenderData> itemDataList = new ArrayList<>();
        for (final GroundItem item : itemSpawner.getActiveItems()) {
            itemDataList.add(new ItemRenderData(
                item.getX(),
                item.getY(),
                item.getWidth(),
                item.getHeight(),
                item.getId(),
                item.isAnimated()
            ));
        }
        itemRenderer.render(gc, itemDataList, frameCount);

        // Player Layer
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        // Foreground Layer
        environmentRenderer.render(gc, new MapRenderData(upperGrid, TILE_SIZE));

        gc.restore(); 

        // --- HUD ---
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), kiki.maxEnergy(), kiki.getMoney());
        hudRenderer.render(gc, hudData);

    }
}
