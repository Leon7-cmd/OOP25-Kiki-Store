package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.KikiStore.model.item.api.Item;
import it.unibo.KikiStore.model.item.api.ItemSpawner;
import it.unibo.KikiStore.model.item.impl.ItemSpawnerImpl;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.entity.impl.EntityRenderer;
import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.environment.impl.MapRenderer;
import it.unibo.KikiStore.view.hud.api.HUDRenderData;
import it.unibo.KikiStore.view.hud.impl.HUDRenderer;
import it.unibo.KikiStore.view.item.api.ItemRenderData;
import it.unibo.KikiStore.view.item.impl.ItemRenderer;
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of GameState used to test player movement,
 * map rendering, dynamic item spawning, and pickup collection.
 */
public final class TestState implements GameState {
    private static final int TILE_SIZE = 32;
    private static final int PLAYER_X = 1850;
    private static final int PLAYER_Y = 2950;
    private static final int ITEM_SPAWN_COUNT = 10;
    private static final int[] TELEPORT1 = {2550, 1520};
    private static final int[] TELEPORT2 = {1280, 2410};

    private static final String INGREDIENTS_PATH = "textFiles/ingredients.json";
    private static final String POTIONS_PATH = "textFiles/potions.json";

    private final InputHandler input;
    private final PlayerImpl kiki; 
    private final Inventory inventory;
    private final ItemSpawner itemSpawner;

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

    public TestState(final GameStateTransition transitionController, final InputHandler input) {
        this.transitionController = transitionController;
        this.input = input;

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/map0/testGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/map0/testDecor.txt");
        this.upperGrid = MapLoader.loadMap("maps/map0/testUpper.txt");
        this.maskGrid = MapLoader.loadMap("maps/map0/col/testCol.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        this.collisionHandler = new CollisionHandler(collisionMap);

        this.kiki = new PlayerImpl(PLAYER_X, PLAYER_Y);
        this.kiki.setCollisionHandler(collisionHandler); 

        // Inizializzazione inventario e catalogo
        this.inventory = new InventoryImpl();
        final GameCatalog catalog = new GameCatalogImpl(INGREDIENTS_PATH, POTIONS_PATH);

        // ItemSpawner riceve il catalogo e popola il mondo
        this.itemSpawner = new ItemSpawnerImpl(collisionMap, catalog.getAllIngredients());
        this.itemSpawner.spawnRandomItems(ITEM_SPAWN_COUNT);

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(this.spriteManager);
        this.environmentRenderer = new MapRenderer(this.spriteManager);
        this.itemRenderer = new ItemRenderer(this.spriteManager);
        this.hudRenderer = new HUDRenderer(this.spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        kiki.update(input);

        // Controlla la collisione tra Kiki e gli item a terra:
        // gli item toccati vengono trasferiti nell'inventario e rimossi dalla lista attiva
        final List<Item> collected = itemSpawner.checkCollection(kiki, inventory);
        if (!collected.isEmpty()) {
            for (final Item item : collected) {
                System.out.println("-> Raccolto item: " + item.getId());
            }

            // Stampa il contenuto attuale dell'inventario
            System.out.println("=== INVENTARIO ATTUALE ===");
            for (final Ingredient ing : inventory.getIngredients()) {
                System.out.println(" - " + ing.getName() + " | Quantità: " + ing.getQuantity() + " | Tipo: " + ing.getType());
            }
            System.out.println("==========================");
        }
        itemSpawner.update();

        frameCount++;

        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);
        if (tileId == 2 && input.isAction()) {
            kiki.setX(TELEPORT1[0]);
            kiki.setY(TELEPORT1[1]);
        }
        if (tileId == 3 && input.isAction()) {
            kiki.setX(TELEPORT2[0]);
            kiki.setY(TELEPORT2[1]);
        }
        if (tileId == 4 && input.isAction()) {
            transitionController.pushState(new MinigameFly(transitionController, input));
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        gc.save(); 
        cam.update(kiki.getX(), kiki.getY(), screenWidth, screenHeight);
        gc.translate(-cam.getX(), -cam.getY()); 

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));

        // Ground Items Layer
        final List<ItemRenderData> itemDataList = new ArrayList<>();
        for (final Item item : itemSpawner.getActiveItems()) {
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
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), 5, kiki.getMoney());
        hudRenderer.render(gc, hudData);
    }
}
